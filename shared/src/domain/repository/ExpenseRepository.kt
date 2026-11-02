package org.moh.raqeem.project.domain.repository

import kotlinx.coroutines.flow.Flow
import org.moh.raqeem.project.domain.model.WeeklyAnalytics
import org.moh.raqeem.project.model.ExpenseItem

interface ExpenseRepository {
    fun getExpensesStream(): Flow<List<ExpenseItem>>
    fun getWeeklyAnalyticsStream(weeklyBudget: Double = 2500.0): Flow<WeeklyAnalytics>
    suspend fun getExpense(id: String): ExpenseItem?
    suspend fun insertExpenses(items: List<ExpenseItem>)
    suspend fun deleteExpense(id: String)
    suspend fun clearAll()
}

