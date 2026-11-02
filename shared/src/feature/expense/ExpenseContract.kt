package org.moh.raqeem.project.feature.expense

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
    val dailyTotal: Double = 0.0,
    val weeklyBudgetTotal: Double = 2500.0,
    val weeklyAnalytics: WeeklyAnalytics? = null,
    val currency: String = "SAR",
    val recordingStatus: RecordingStatus = RecordingStatus.IDLE,
    val recordingAmplitude: Float = 0f,
    val recordingDurationSec: Int = 0,
    val pendingReviewBatch: ExpenseBatch? = null,
    val recentExpenses: List<ExpenseItem> = emptyList(),
    val isBankNotificationSheetOpen: Boolean = false,
    val isApiKeyDialogOpen: Boolean = false,
    val isOnlineAiActive: Boolean = false,
    val errorMessage: String? = null
)

sealed interface ExpenseAction {
    data object StartVoiceRecording : ExpenseAction
    data object StopVoiceRecording : ExpenseAction
    data object CancelVoiceRecording : ExpenseAction
    data class ConfirmBatch(val batch: ExpenseBatch) : ExpenseAction
    data object DismissPendingBatch : ExpenseAction
    data class DeleteExpense(val id: String) : ExpenseAction
    data class SubmitSimulatedVoice(val voiceText: String) : ExpenseAction
    data class ParseBankNotification(val text: String) : ExpenseAction
    data class SetBankNotificationSheetVisible(val visible: Boolean) : ExpenseAction
    data class SetApiKeyDialogVisible(val visible: Boolean) : ExpenseAction
    data class SaveApiKey(val apiKey: String) : ExpenseAction
}

sealed interface ExpenseEffect {
    data class ShowSnackbar(val message: String) : ExpenseEffect
    data object RequestAudioPermission : ExpenseEffect
}
