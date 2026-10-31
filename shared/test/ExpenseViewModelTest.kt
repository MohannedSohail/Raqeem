package org.moh.raqeem.project

import org.moh.raqeem.project.feature.expense.ExpenseAction
import org.moh.raqeem.project.feature.expense.ExpenseViewModel
import org.moh.raqeem.project.model.ExpenseBatch
import org.moh.raqeem.project.model.ExpenseCategory
import org.moh.raqeem.project.model.ExpenseItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ExpenseViewModelTest {

    @Test
    fun initialStateHasDefaultValues() {
        val vm = ExpenseViewModel()
        val state = vm.state.value

        assertEquals(135.0, state.dailyTotal)
        assertEquals(2000.0, state.weeklyBudgetTotal)
        assertEquals("SAR", state.currency)
        assertEquals(2, state.recentExpenses.size)
        assertNull(state.pendingReviewBatch)
    }

    @Test
    fun confirmingBatchAddsItemsAndUpdatesDailyTotal() {
        val vm = ExpenseViewModel()
        val initialTotal = vm.state.value.dailyTotal

        val testBatch = ExpenseBatch(
            totalAmount = 150.0,
            currency = "SAR",
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
        assertEquals(initialTotal + 150.0, updatedState.dailyTotal)
        assertEquals(3, updatedState.recentExpenses.size)
        assertEquals("عشاء مطعم", updatedState.recentExpenses.first().title)
        assertNull(updatedState.pendingReviewBatch)
    }

    @Test
    fun deletingExpenseReducesDailyTotal() {
        val vm = ExpenseViewModel()
        val targetItem = vm.state.value.recentExpenses.first()
        val previousTotal = vm.state.value.dailyTotal

        vm.onAction(ExpenseAction.DeleteExpense(targetItem.id))

        val updatedState = vm.state.value
        assertEquals(previousTotal - targetItem.amount, updatedState.dailyTotal)
        assertEquals(1, updatedState.recentExpenses.size)
        assertTrue(updatedState.recentExpenses.none { it.id == targetItem.id })
    }

    @Test
    fun dismissingPendingBatchClearsWithoutTotalChange() {
        val vm = ExpenseViewModel()
        val initialTotal = vm.state.value.dailyTotal

        vm.onAction(ExpenseAction.DismissPendingBatch)

        val updatedState = vm.state.value
        assertNull(updatedState.pendingReviewBatch)
        assertEquals(initialTotal, updatedState.dailyTotal)
    }
}

