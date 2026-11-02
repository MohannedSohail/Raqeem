package org.moh.raqeem.project.feature.expense

import org.moh.raqeem.project.ai.whisper.WhisperModelSize
import org.moh.raqeem.project.domain.model.WeeklyAnalytics
import org.moh.raqeem.project.model.ExpenseBatch
import org.moh.raqeem.project.model.ExpenseItem

enum class RecordingStatus {
    IDLE,
    RECORDING,
    PROCESSING,
    SUCCESS
}

data class ExpenseState(
    val dailyTotal: Double = 135.0,
    val weeklyBudgetTotal: Double = 2500.0,
    val weeklyAnalytics: WeeklyAnalytics? = null,
    val currency: String = "شيكل",
    val recordingStatus: RecordingStatus = RecordingStatus.IDLE,
    val recordingAmplitude: Float = 0f,
    val recordingDurationSec: Int = 0,
    val pendingReviewBatch: ExpenseBatch? = null,
    val recentExpenses: List<ExpenseItem> = emptyList(),
    val isBankNotificationSheetOpen: Boolean = false,
    val isVoiceInputSheetOpen: Boolean = false,
    val isEngineSettingsOpen: Boolean = false,
    val isLocalWhisperActive: Boolean = true,
    val activeWhisperModel: WhisperModelSize = WhisperModelSize.TINY,
    val lastWhisperTranscript: String? = null,
    val errorMessage: String? = null
)

sealed interface ExpenseAction {
    data object StartVoiceRecording : ExpenseAction
    data object StopVoiceRecording : ExpenseAction
    data object CancelVoiceRecording : ExpenseAction
    data class ConfirmBatch(val batch: ExpenseBatch) : ExpenseAction
    data object DismissPendingBatch : ExpenseAction
    data class DeleteExpense(val id: String) : ExpenseAction
    data class SubmitRealVoiceText(val voiceText: String) : ExpenseAction
    data class ParseBankNotification(val text: String) : ExpenseAction
    data class SetBankNotificationSheetVisible(val visible: Boolean) : ExpenseAction
    data class SetVoiceInputSheetVisible(val visible: Boolean) : ExpenseAction
    data class SetEngineSettingsVisible(val visible: Boolean) : ExpenseAction
    data class SelectWhisperModel(val modelSize: WhisperModelSize) : ExpenseAction
}

sealed interface ExpenseEffect {
    data class ShowSnackbar(val message: String) : ExpenseEffect
    data object RequestAudioPermission : ExpenseEffect
}
