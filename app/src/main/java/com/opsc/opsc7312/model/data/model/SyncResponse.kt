package com.opsc.opsc7312.model.data.model

data class SyncResponse(
    var message: String = "",
    var success: Boolean = false,
    val ids: List<IdMapping> = listOf()
)
