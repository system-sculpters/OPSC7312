package com.opsc.opsc7312.model.data.offline.schema

object TransactionSchema {
    const val TABLE_NAME = "transactions"
    const val COLUMN_ID = "id"
    const val COLUMN_NAME = "name"
    const val COLUMN_AMOUNT = "amount"
    const val COLUMN_DATE = "date"
    const val COLUMN_USER_ID = "userid"
    const val COLUMN_IS_RECURRING = "isrecurring"
    const val COLUMN_TYPE = "type"
    const val COLUMN_CATEGORY_ID = "categoryId"
    const val COLUMN_SYNC_STATUS = "sync_status"

    const val CREATE_TABLE =  """
            CREATE TABLE ${TABLE_NAME} (
                ${COLUMN_ID} TEXT PRIMARY KEY,
                ${COLUMN_NAME} TEXT,
                ${COLUMN_AMOUNT} REAL,
                ${COLUMN_DATE} INTEGER,
                ${COLUMN_USER_ID} TEXT,
                ${COLUMN_IS_RECURRING} INTEGER,
                ${COLUMN_TYPE} TEXT,
                ${COLUMN_CATEGORY_ID} TEXT,
                ${COLUMN_SYNC_STATUS} INTEGER
            )
        """
}