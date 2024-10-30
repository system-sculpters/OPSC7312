package com.opsc.opsc7312.model.data.model

data class Notification(
    var id: String = "",
    var title: String = "",
    var message: String = "",
    var status: Boolean = false,
    var createdAt: Long = 0L
)
