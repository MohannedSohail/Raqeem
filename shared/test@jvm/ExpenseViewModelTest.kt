package org.moh.raqeem.project

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.moh.raqeem.project.ai.whisper.WhisperModelSize
import org.moh.raqeem.project.data.repository.DefaultExpenseRepository
import org.moh.raqeem.project.feature.expense.ExpenseAction
import org.moh.raqeem.project.feature.expense.ExpenseViewModel
import org.moh.raqeem.project.feature.expense.RecordingStatus
import org.moh.raqeem.project.model.ExpenseBatch
import org.moh.raqeem.project.model.ExpenseCategory
import org.moh.raqeem.project.model.ExpenseItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ExpenseViewModelTest {

    private fun createTestViewModel(repo: DefaultExpenseRepository = DefaultExpenseRepository()): ExpenseViewModel {
        val testScope = CoroutineScope(Dispatchers.Unconfined)
        return ExpenseViewModel(repository = repo, customScope = testScope)
    }

    @Test
    fun initialStateHasDefaultValuesWithShekelCurrency() {
        val vm = createTestViewModel()
        val state = vm.state.value

        assertEquals(135.0, state.dailyTotal)
        assertEquals(2500.0, state.weeklyBudgetTotal)
        assertEquals("شيكل", state.currency, "Currency must be Shekel (شيكل)")
        assertEquals(3, state.recentExpenses.size)
        assertNull(state.pendingReviewBatch)
        assertTrue(state.isLocalWhisperActive, "Local Whisper engine must be active by default")
        assertEquals(WhisperModelSize.TINY, state.activeWhisperModel)
    }

    @Test
    fun realVoiceTextProcessingProducesDynamicBatchInShekels() = runBlocking {
        val vm = createTestViewModel()

        vm.onAction(ExpenseAction.SubmitRealVoiceText("عبيت بنزين 91 بخمسين شيكل ومحمد حاسب قهوة بـ 18"))

        val state = vm.state.value
        assertEquals(RecordingStatus.SUCCESS, state.recordingStatus)
        assertNotNull(state.pendingReviewBatch)
        assertEquals(2, state.pendingReviewBatch.items.size)
        assertEquals(68.0, state.pendingReviewBatch.totalAmount)
        assertEquals("شيكل", state.pendingReviewBatch.currency)
        assertTrue(state.pendingReviewBatch.sourceEngine.contains("Whisper.cpp"))
    }

    @Test
    fun bankSmsParsingExtractsLocallyInShekels() = runBlocking {
        val vm = createTestViewModel()

        vm.onAction(ExpenseAction.ParseBankNotification("شراء بقيمة 120 شيكل من مطعم بطاقة بنكية"))

        val state = vm.state.value
        assertNotNull(state.pendingReviewBatch)
        assertEquals(120.0, state.pendingReviewBatch.totalAmount)
        assertEquals("شيكل", state.pendingReviewBatch.currency)
        assertEquals(ExpenseCategory.DINING, state.pendingReviewBatch.items.first().category)
    }

    @Test
    fun switchingWhisperModelSizeUpdatesState() {
        val vm = createTestViewModel()

        vm.onAction(ExpenseAction.SelectWhisperModel(WhisperModelSize.BASE))

        val state = vm.state.value
        assertEquals(WhisperModelSize.BASE, state.activeWhisperModel)
    }

    @Test
    fun confirmingBatchAddsItemsAndUpdatesDailyTotal() = runBlocking {
        val repo = DefaultExpenseRepository()
        val vm = createTestViewModel(repo)

        val testBatch = ExpenseBatch(
            totalAmount = 150.0,
            currency = "شيكل",
            items = listOf(
                ExpenseItem(
                    id = "test_item_1",
                    title = "عشاء مطعم",
                    amount = 150.0,
                    category = ExpenseCategory.DINING,
                    paidBy = "أحمد",
                    beneficiary = "شخصي",
                    dateIso = "2026-10-31"
                )
            ),
            confidence = 0.98f
        )

        vm.onAction(ExpenseAction.ConfirmBatch(testBatch))

        val updatedState = vm.state.value
        assertEquals(285.0, updatedState.dailyTotal)
        assertEquals(4, updatedState.recentExpenses.size)
        assertEquals("عشاء مطعم", updatedState.recentExpenses.first().title)
        assertNull(updatedState.pendingReviewBatch)
    }

    @Test
    fun deletingExpenseReducesCount() = runBlocking {
        val repo = DefaultExpenseRepository()
        val vm = createTestViewModel(repo)
        val targetItem = vm.state.value.recentExpenses.first()

        vm.onAction(ExpenseAction.DeleteExpense(targetItem.id))

        val updatedState = vm.state.value
        assertEquals(2, updatedState.recentExpenses.size)
        assertTrue(updatedState.recentExpenses.none { it.id == targetItem.id })
    }

    @Test
    fun dismissingPendingBatchClearsWithoutTotalChange() {
        val vm = createTestViewModel()
        val initialTotal = vm.state.value.dailyTotal

        vm.onAction(ExpenseAction.DismissPendingBatch)

        val updatedState = vm.state.value
        assertNull(updatedState.pendingReviewBatch)
        assertEquals(initialTotal, updatedState.dailyTotal)
    }
}
