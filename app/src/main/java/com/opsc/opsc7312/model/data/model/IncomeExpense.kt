package com.opsc.opsc7312.model.data.model

// Data class representing a IncomeExpense entity

// This class was adapted from medium
//https://medium.com/androiddevelopers/data-classes-the-classy-way-to-hold-data-ab3b11ea4939
//Florina Muntenescu
//https://medium.com/@florina.muntenescu
data class IncomeExpense(
    val label: String,
    val income: Double,
    val expense: Double,
    val count: Int
)
