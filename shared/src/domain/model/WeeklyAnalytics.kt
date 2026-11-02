package org.moh.raqeem.project.domain.model

import org.moh.raqeem.project.model.ExpenseCategory

data class WeeklyAnalytics(
    val totalSpent: Double,
    val weeklyBudget: Double,
    val budgetRemaining: Double,
    val budgetUsagePercentage: Float,
    val topCategory: ExpenseCategory?,
    val topCategoryAmount: Double,
    val payerDistribution: Map<String, Double>,
    val totalTransactionsCount: Int
)

