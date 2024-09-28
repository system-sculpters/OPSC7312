package com.opsc.opsc7312.model.data.model

// Data class representing a AnalyticsResponse entity

// This class was adapted from medium
//https://medium.com/androiddevelopers/data-classes-the-classy-way-to-hold-data-ab3b11ea4939
//Florina Muntenescu
//https://medium.com/@florina.muntenescu
data class AnalyticsResponse(
    // A list of IncomeExpense objects representing transactions grouped by month.
    val transactionsByMonth: List<IncomeExpense>,

    // A list of IncomeExpense objects representing daily transactions.
    val dailyTransactions: List<IncomeExpense>,

    // A list of CategoryExpense objects representing statistics for different expense categories.
    val categoryStats: List<CategoryExpense>,

    // A list of Goal objects representing the user's financial goals.
    val goals: List<Goal>
)
