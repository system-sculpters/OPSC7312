package com.opsc.opsc7312.model.data.model

data class IdMapping(
    val localId: String,  // Local ID used in the app before sync
    val firebaseId: String
)
