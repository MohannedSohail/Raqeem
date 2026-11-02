package org.moh.raqeem.project.feature.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.moh.raqeem.project.ai.GeminiApiClient
import org.moh.raqeem.project.audio.AudioRecorder
import org.moh.raqeem.project.data.repository.DefaultExpenseRepository
import org.moh.raqeem.project.domain.repository.ExpenseRepository
import org.moh.raqeem.project.model.ExpenseBatch
import org.moh.raqeem.project.model.ExpenseCategory
import org.moh.raqeem.project.model.ExpenseItem
import org.moh.raqeem.project.model.RecordingResult

class ExpenseViewModel(
    private val repository: ExpenseRepository = DefaultExpenseRepository(),
    private val audioRecorder: AudioRecorder = AudioRecorder(),
    private val geminiApiClient: GeminiApiClient = GeminiApiClient(),
    customScope: CoroutineScope? = null
) : ViewModel() {

    private val vmScope: CoroutineScope by lazy {
        customScope ?: viewModelScope
    }

    private val _state = MutableStateFlow(
        ExpenseState(
            dailyTotal = 135.0,
            weeklyBudgetTotal = 2500.0,
            isOnlineAiActive = geminiApiClient.hasApiKey()
        )
    )
    val state: StateFlow<ExpenseState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ExpenseEffect>()
    val effect: SharedFlow<ExpenseEffect> = _effect.asSharedFlow()

    private var recordingTickerJob: Job? = null

    init {
        // Observe expenses stream as Single Source of Truth
        vmScope.launch {
            repository.getExpensesStream().collect { items ->
                val todayTotal = items.filter { it.dateIso == "2026-10-31" }.sumOf { it.amount }
                _state.update {
                    it.copy(
                        recentExpenses = items,
                        dailyTotal = todayTotal
                    )
                }
            }
        }

        // Observe weekly analytics stream
        vmScope.launch {
            repository.getWeeklyAnalyticsStream(_state.value.weeklyBudgetTotal).collect { analytics ->
                _state.update { it.copy(weeklyAnalytics = analytics) }
            }
        }
    }

    fun onAction(action: ExpenseAction) {
        when (action) {
            is ExpenseAction.StartVoiceRecording -> startRecording()
            is ExpenseAction.StopVoiceRecording -> stopRecording()
            is ExpenseAction.CancelVoiceRecording -> cancelRecording()
            is ExpenseAction.ConfirmBatch -> confirmBatch(action.batch)
            is ExpenseAction.DismissPendingBatch -> dismissPendingBatch()
            is ExpenseAction.DeleteExpense -> deleteExpense(action.id)
            is ExpenseAction.SubmitSimulatedVoice -> processVoiceText(action.voiceText)
            is ExpenseAction.ParseBankNotification -> parseBankNotification(action.text)
            is ExpenseAction.SetBankNotificationSheetVisible -> {
                _state.update { it.copy(isBankNotificationSheetOpen = action.visible) }
            }
            is ExpenseAction.SetApiKeyDialogVisible -> {
                _state.update { it.copy(isApiKeyDialogOpen = action.visible) }
            }
            is ExpenseAction.SaveApiKey -> {
                geminiApiClient.setApiKey(action.apiKey)
                _state.update {
                    it.copy(
                        isApiKeyDialogOpen = false,
                        isOnlineAiActive = action.apiKey.isNotBlank()
                    )
                }
                vmScope.launch {
                    val statusText = if (action.apiKey.isNotBlank()) "تم تفعيل Gemini 2.5 Flash السحابي ⚡" else "تم التبديل للوضع المحلي"
                    _effect.emit(ExpenseEffect.ShowSnackbar(statusText))
                }
            }
        }
    }

    private fun startRecording() {
        try {
            audioRecorder.startRecording("raqeem_voice_temp.mp4")
            _state.update {
                it.copy(
                    recordingStatus = RecordingStatus.RECORDING,
                    recordingDurationSec = 0,
                    recordingAmplitude = 0.1f,
                    errorMessage = null
                )
            }

            recordingTickerJob?.cancel()
            recordingTickerJob = vmScope.launch {
                var ticks = 0
                while (_state.value.recordingStatus == RecordingStatus.RECORDING) {
                    delay(100)
                    ticks++
                    val amp = audioRecorder.getAmplitude()
                    _state.update {
                        it.copy(
                            recordingAmplitude = amp,
                            recordingDurationSec = ticks / 10
                        )
                    }
                }
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    recordingStatus = RecordingStatus.IDLE,
                    errorMessage = "تعذر بدء التسجيل: تأكد من إذن الميكروفون"
                )
            }
        }
    }

    private fun stopRecording() {
        recordingTickerJob?.cancel()
        val result = audioRecorder.stopRecording() ?: RecordingResult("stub.mp4", 1500L)

        _state.update {
            it.copy(
                recordingStatus = RecordingStatus.PROCESSING,
                recordingAmplitude = 0f
            )
        }

        vmScope.launch {
            val generatedBatch = geminiApiClient.parseVoiceAudio(result)

            _state.update {
                it.copy(
                    recordingStatus = RecordingStatus.SUCCESS,
                    pendingReviewBatch = generatedBatch
                )
            }
        }
    }

    private fun cancelRecording() {
        recordingTickerJob?.cancel()
        audioRecorder.cancelRecording()
        _state.update {
            it.copy(
                recordingStatus = RecordingStatus.IDLE,
                recordingAmplitude = 0f,
                recordingDurationSec = 0
            )
        }
    }

    private fun processVoiceText(text: String) {
        _state.update { it.copy(recordingStatus = RecordingStatus.PROCESSING) }
        vmScope.launch {
            val batch = geminiApiClient.parseTextOrSms(text)
            _state.update {
                it.copy(
                    recordingStatus = RecordingStatus.SUCCESS,
                    pendingReviewBatch = batch
                )
            }
        }
    }

    private fun parseBankNotification(text: String) {
        if (text.isBlank()) return
        _state.update {
            it.copy(
                isBankNotificationSheetOpen = false,
                recordingStatus = RecordingStatus.PROCESSING
            )
        }
        vmScope.launch {
            val batch = geminiApiClient.parseTextOrSms(text)
            _state.update {
                it.copy(
                    recordingStatus = RecordingStatus.SUCCESS,
                    pendingReviewBatch = batch
                )
            }
        }
    }

    private fun confirmBatch(batch: ExpenseBatch) {
        vmScope.launch {
            repository.insertExpenses(batch.items)
            _state.update {
                it.copy(
                    pendingReviewBatch = null,
                    recordingStatus = RecordingStatus.IDLE
                )
            }
            _effect.emit(ExpenseEffect.ShowSnackbar("تم حفظ ${batch.items.size} مصاريف بنجاح!"))
        }
    }

    private fun dismissPendingBatch() {
        _state.update {
            it.copy(
                pendingReviewBatch = null,
                recordingStatus = RecordingStatus.IDLE
            )
        }
    }

    private fun deleteExpense(id: String) {
        vmScope.launch {
            repository.deleteExpense(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        recordingTickerJob?.cancel()
        audioRecorder.cancelRecording()
    }
}
