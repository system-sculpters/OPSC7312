package com.opsc.opsc7312.model.data.model

data class InvestmentResponse(
    val balance: Double = 0.0,
    val investments: List<Investment> = listOf()
)
