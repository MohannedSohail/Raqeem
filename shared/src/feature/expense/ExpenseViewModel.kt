package org.moh.raqeem.project.feature.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import org.moh.raqeem.project.audio.AudioRecorder
import org.moh.raqeem.project.model.ExpenseBatch
import org.moh.raqeem.project.model.ExpenseCategory
import org.moh.raqeem.project.model.ExpenseItem

class ExpenseViewModel(
    private val audioRecorder: AudioRecorder = AudioRecorder()
) : ViewModel() {

    private val _state = MutableStateFlow(
        ExpenseState(
            dailyTotal = 135.0,
            weeklyBudgetTotal = 2000.0,
            recentExpenses = listOf(
                ExpenseItem(
                    id = "1",
                    title = "قهوة صباحية ومخبوزات",
                    amount = 35.0,
                    category = ExpenseCategory.DINING,
                    paidBy = "أنا",
                    beneficiary = "شخصي",
                    dateIso = "2026-10-31"
                ),
                ExpenseItem(
                    id = "2",
                    title = "بنزين السيارة",
                    amount = 100.0,
                    category = ExpenseCategory.TRANSPORTATION,
                    paidBy = "أنا",
                    beneficiary = "شخصي",
                    dateIso = "2026-10-31"
                )
            )
        )
    )
    val state: StateFlow<ExpenseState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ExpenseEffect>()
    val effect: SharedFlow<ExpenseEffect> = _effect.asSharedFlow()

    private var recordingTickerJob: Job? = null

    fun onAction(action: ExpenseAction) {
        when (action) {
            is ExpenseAction.StartVoiceRecording -> startRecording()
            is ExpenseAction.StopVoiceRecording -> stopRecording()
            is ExpenseAction.CancelVoiceRecording -> cancelRecording()
            is ExpenseAction.ConfirmBatch -> confirmBatch(action.batch)
            is ExpenseAction.DismissPendingBatch -> dismissPendingBatch()
            is ExpenseAction.DeleteExpense -> deleteExpense(action.id)
            is ExpenseAction.SubmitSimulatedVoice -> processVoiceText(action.voiceText)
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
            recordingTickerJob = viewModelScope.launch {
                var seconds = 0
                while (_state.value.recordingStatus == RecordingStatus.RECORDING) {
                    delay(100)
                    val amp = audioRecorder.getAmplitude()
                    _state.update { it.copy(recordingAmplitude = amp) }
                    if (System.currentTimeMillis() % 1000 < 100) {
                        seconds++
                        _state.update { it.copy(recordingDurationSec = seconds) }
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
        val result = audioRecorder.stopRecording()

        _state.update {
            it.copy(
                recordingStatus = RecordingStatus.PROCESSING,
                recordingAmplitude = 0f
            )
        }

        viewModelScope.launch {
            // Simulate AI parsing delay (Gemini Flash latency ~1.2s)
            delay(1200)

            // Extracted smart batch from the recorded voice
            val generatedBatch = ExpenseBatch(
                totalAmount = 205.0,
                currency = _state.value.currency,
                items = listOf(
                    ExpenseItem(
                        id = "batch_${System.currentTimeMillis()}_1",
                        title = "أغراض سوبرماركت كارفور",
                        amount = 180.0,
                        category = ExpenseCategory.GROCERIES,
                        paidBy = "أنا",
                        beneficiary = "البيت",
                        dateIso = "2026-10-31"
                    ),
                    ExpenseItem(
                        id = "batch_${System.currentTimeMillis()}_2",
                        title = "بنزين سيارة",
                        amount = 25.0,
                        category = ExpenseCategory.TRANSPORTATION,
                        paidBy = "أنا",
                        beneficiary = "شخصي",
                        dateIso = "2026-10-31"
                    )
                ),
                notes = "محمد حاسب 40 قهوة منفصلة",
                confidence = 0.96f
            )

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
        viewModelScope.launch {
            delay(800)
            val batch = ExpenseBatch(
                totalAmount = 85.0,
                currency = _state.value.currency,
                items = listOf(
                    ExpenseItem(
                        id = "text_${System.currentTimeMillis()}",
                        title = text.ifBlank { "مشتريات متنوعة" },
                        amount = 85.0,
                        category = ExpenseCategory.DINING,
                        paidBy = "أنا",
                        beneficiary = "شخصي",
                        dateIso = "2026-10-31"
                    )
                ),
                confidence = 0.92f
            )
            _state.update {
                it.copy(
                    recordingStatus = RecordingStatus.SUCCESS,
                    pendingReviewBatch = batch
                )
            }
        }
    }

    private fun confirmBatch(batch: ExpenseBatch) {
        _state.update { current ->
            val updatedList = batch.items + current.recentExpenses
            val addedAmount = batch.items.sumOf { it.amount }
            current.copy(
                recentExpenses = updatedList,
                dailyTotal = current.dailyTotal + addedAmount,
                pendingReviewBatch = null,
                recordingStatus = RecordingStatus.IDLE
            )
        }
        viewModelScope.launch {
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
        _state.update { current ->
            val item = current.recentExpenses.find { it.id == id }
            val removedAmount = item?.amount ?: 0.0
            current.copy(
                recentExpenses = current.recentExpenses.filterNot { it.id == id },
                dailyTotal = (current.dailyTotal - removedAmount).coerceAtLeast(0.0)
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        recordingTickerJob?.cancel()
        audioRecorder.cancelRecording()
    }
}

