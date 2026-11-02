package org.moh.raqeem.project

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.moh.raqeem.project.data.repository.DefaultExpenseRepository
import org.moh.raqeem.project.model.ExpenseCategory
import org.moh.raqeem.project.model.ExpenseItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExpenseRepositoryTest {

    @Test
    fun repositoryEmitsInitialTransactions() = runBlocking {
        val repo = DefaultExpenseRepository()
        val items = repo.getExpensesStream().first()

        assertEquals(3, items.size)
        assertTrue(items.any { it.title.contains("قهوة") })
    }

    @Test
    fun insertExpensesAppendsToStream() = runBlocking {
        val repo = DefaultExpenseRepository()
        val newItem = ExpenseItem(
            id = "new_test_1",
            title = "فاتورة مياه",
            amount = 90.0,
            category = ExpenseCategory.HOME_UTILITIES,
            paidBy = "أنا",
            beneficiary = "البيت",
            dateIso = "2026-10-31"
        )

        repo.insertExpenses(listOf(newItem))
        val updated = repo.getExpensesStream().first()

        assertEquals(4, updated.size)
        assertEquals("فاتورة مياه", updated.first().title)
    }

    @Test
    fun deleteExpenseRemovesFromStream() = runBlocking {
        val repo = DefaultExpenseRepository()
        val initial = repo.getExpensesStream().first()
        val targetId = initial.first().id

        repo.deleteExpense(targetId)
        val updated = repo.getExpensesStream().first()

        assertEquals(2, updated.size)
        assertTrue(updated.none { it.id == targetId })
    }

    @Test
    fun weeklyAnalyticsComputesTopCategoryAndPayerDistribution() = runBlocking {
        val repo = DefaultExpenseRepository()
        val analytics = repo.getWeeklyAnalyticsStream(weeklyBudget = 2000.0).first()

        // 35 (dining) + 100 (transport) + 260 (groceries) = 395
        assertEquals(395.0, analytics.totalSpent)
        assertEquals(1605.0, analytics.budgetRemaining)
        assertEquals(ExpenseCategory.GROCERIES, analytics.topCategory)
        assertEquals(260.0, analytics.topCategoryAmount)

        // Payer distribution
        assertEquals(135.0, analytics.payerDistribution["أنا"])
        assertEquals(260.0, analytics.payerDistribution["محمد"])
    }
}

