package com.opsc.opsc7312.model.data.offline.schema

object CategorySchema {
    const val TABLE_NAME = "category"
    const val COLUMN_ID = "id"
    const val COLUMN_NAME = "name"
    const val COLUMN_COLOR = "color"
    const val COLUMN_ICON = "icon"
    const val COLUMN_TRANSACTION_TYPE = "transactiontype"
    const val COLUMN_USER_ID = "userid"
    const val COLUMN_SYNC_STATUS = "sync_status"

    const val CREATE_TABLE =  "CREATE TABLE ${TABLE_NAME} (" +
            "${COLUMN_ID} TEXT PRIMARY KEY, " +
            "${COLUMN_NAME} TEXT, " +
            "${COLUMN_COLOR} TEXT, " +
            "${COLUMN_ICON} TEXT, " +
            "${COLUMN_TRANSACTION_TYPE} TEXT, " +
            "${COLUMN_USER_ID} TEXT, " +
            "${COLUMN_SYNC_STATUS} INTEGER" +  // 0 = not synced, 1 = synced
            ")"
}