package com.opsc.opsc7312.model.data.offline.schema

import android.util.Log

object GoalSchema {
    const val TABLE_NAME = "goal"
    const val COLUMN_ID = "id"
    const val COLUMN_USER_ID = "userid"
    const val COLUMN_NAME = "name"
    const val COLUMN_TARGET_AMOUNT = "targetamount"
    const val COLUMN_CURRENT_AMOUNT = "currentamount"
    const val COLUMN_DEADLINE = "deadline"
    const val COLUMN_CONTRIBUTION_TYPE = "contributiontype"
    const val COLUMN_CONTRIBUTION_AMOUNT = "contributionamount"
    const val COLUMN_SYNC_STATUS = "sync_status"

    val CREATE_TABLE = "CREATE TABLE ${TABLE_NAME} (" +
            "${COLUMN_ID} TEXT PRIMARY KEY, " +
            "${COLUMN_USER_ID} TEXT, " +
            "${COLUMN_NAME} TEXT, " +
            "${COLUMN_TARGET_AMOUNT} REAL, " +
            "${COLUMN_CURRENT_AMOUNT} REAL, " +
            "${COLUMN_DEADLINE} INTEGER, " + // Use INTEGER for Unix timestamp
            "${COLUMN_CONTRIBUTION_TYPE} TEXT, " +
            "${COLUMN_CONTRIBUTION_AMOUNT} REAL, " +
            "${COLUMN_SYNC_STATUS} INTEGER" +  // 0 = not synced, 1 = synced
            ")"

}