package com.opsc.opsc7312.model.data.model

// Data class representing a CategoryExpense entity

// This class was adapted from medium
//https://medium.com/androiddevelopers/data-classes-the-classy-way-to-hold-data-ab3b11ea4939
//Florina Muntenescu
//https://medium.com/@florina.muntenescu

// Data class representing an expense category along with its associated data.
data class CategoryExpense(
    val categoryId: String,      // Unique ID for the category
    val name: String,            // Name of the category
    val color: String,           // Color of the category (for UI purposes)
    val totalAmount: Double,     // Total amount spent in this category
    val transactionCount: Int    // Total number of transactions in this category
)
