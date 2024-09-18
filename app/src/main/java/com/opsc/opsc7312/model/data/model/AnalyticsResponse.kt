package com.opsc.opsc7312.model.data.model

data class AnalyticsResponse(
    val transactionsByMonth: List<IncomeExpense>,
    val dailyTransactions: List<IncomeExpense>,
    val categoryStats: List<CategoryExpense>,
    val goals: List<Goal>
)
