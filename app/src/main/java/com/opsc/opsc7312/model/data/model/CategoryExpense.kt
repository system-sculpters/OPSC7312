package com.opsc.opsc7312.model.data.model

data class CategoryExpense(
    val categoryId: String,
    val name: String,
    val color: String,
    val totalAmount: Double,
    val transactionCount: Int
)
