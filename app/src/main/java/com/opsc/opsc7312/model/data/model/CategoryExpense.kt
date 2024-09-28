package com.opsc.opsc7312.model.data.model

// Data class representing a CategoryExpense entity

// This class was adapted from medium
//https://medium.com/androiddevelopers/data-classes-the-classy-way-to-hold-data-ab3b11ea4939
//Florina Muntenescu
//https://medium.com/@florina.muntenescu
data class CategoryExpense(
    val categoryId: String,
    val name: String,
    val color: String,
    val totalAmount: Double,
    val transactionCount: Int
)
