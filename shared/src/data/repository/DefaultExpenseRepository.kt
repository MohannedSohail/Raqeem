package org.moh.raqeem.project.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import org.moh.raqeem.project.domain.model.WeeklyAnalytics
import org.moh.raqeem.project.domain.repository.ExpenseRepository
import org.moh.raqeem.project.model.ExpenseCategory
import org.moh.raqeem.project.model.ExpenseItem

class DefaultExpenseRepository : ExpenseRepository {

    private val _expenses = MutableStateFlow<List<ExpenseItem>>(
        listOf(
            ExpenseItem(
                id = "init_1",
                title = "قهوة صباحية ومخبوزات",
                amount = 35.0,
                category = ExpenseCategory.DINING,
                paidBy = "أنا",
                beneficiary = "شخصي",
                dateIso = "2026-10-31"
            ),
            ExpenseItem(
                id = "init_2",
                title = "بنزين السيارة",
                amount = 100.0,
                category = ExpenseCategory.TRANSPORTATION,
                paidBy = "أنا",
                beneficiary = "شخصي",
                dateIso = "2026-10-31"
            ),
            ExpenseItem(
                id = "init_3",
                title = "مقاضي أسبوعية للبيت",
                amount = 260.0,
                category = ExpenseCategory.GROCERIES,
                paidBy = "محمد",
                beneficiary = "البيت",
                dateIso = "2026-10-30"
            )
        )
    )

    override fun getExpensesStream(): Flow<List<ExpenseItem>> = _expenses.asStateFlow()

    override fun getWeeklyAnalyticsStream(weeklyBudget: Double): Flow<WeeklyAnalytics> {
        return _expenses.map { items ->
            val total = items.sumOf { it.amount }
            val remaining = (weeklyBudget - total).coerceAtLeast(0.0)
            val usage = if (weeklyBudget > 0.0) (total / weeklyBudget).coerceIn(0.0, 1.0).toFloat() else 0f

            // Calculate spending by category
            val categorySums = items.groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

            val topCategoryEntry = categorySums.maxByOrNull { it.value }

            // Calculate payer distribution ("مين دفع كم")
            val payerSums = items.groupBy { it.paidBy }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

            WeeklyAnalytics(
                totalSpent = total,
                weeklyBudget = weeklyBudget,
                budgetRemaining = remaining,
                budgetUsagePercentage = usage,
                topCategory = topCategoryEntry?.key,
                topCategoryAmount = topCategoryEntry?.value ?: 0.0,
                payerDistribution = payerSums,
                totalTransactionsCount = items.size
            )
        }
    }

    override suspend fun getExpense(id: String): ExpenseItem? {
        return _expenses.value.find { it.id == id }
    }

    override suspend fun insertExpenses(items: List<ExpenseItem>) {
        if (items.isEmpty()) return
        val current = _expenses.value
        _expenses.value = items + current
    }

    override suspend fun deleteExpense(id: String) {
        val current = _expenses.value
        _expenses.value = current.filterNot { it.id == id }
    }

    override suspend fun clearAll() {
        _expenses.value = emptyList()
    }
}

