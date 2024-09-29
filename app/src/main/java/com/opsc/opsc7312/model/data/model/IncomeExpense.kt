package com.opsc.opsc7312.model.data.model

// Data class representing an income and expense summary for a specific category or time period.

// This class was adapted from medium
//https://medium.com/androiddevelopers/data-classes-the-classy-way-to-hold-data-ab3b11ea4939
//Florina Muntenescu
//https://medium.com/@florina.muntenescu
data class IncomeExpense(
    val label: String,     // Label for the income/expense category (e.g., "January" or "Food")
    val income: Double,    // Total income for the associated label
    val expense: Double,   // Total expenses for the associated label
    val count: Int         // Number of transactions associated with this label
)
