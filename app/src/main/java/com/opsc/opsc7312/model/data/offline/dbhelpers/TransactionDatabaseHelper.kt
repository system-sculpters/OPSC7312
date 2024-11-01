package com.opsc.opsc7312.model.data.offline.dbhelpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.model.data.offline.schema.CategorySchema
import com.opsc.opsc7312.model.data.offline.schema.TransactionSchema

class TransactionDatabaseHelper(context: Context){
    private val dbHelper = DatabaseHelperProvider(context)

    // Transaction Management
    fun addTransaction(transaction: Transaction): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(TransactionSchema.COLUMN_ID, transaction.id)
            put(TransactionSchema.COLUMN_NAME, transaction.name)
            put(TransactionSchema.COLUMN_AMOUNT, transaction.amount)
            put(TransactionSchema.COLUMN_DATE, transaction.date)
            put(TransactionSchema.COLUMN_USER_ID, transaction.userid)
            put(TransactionSchema.COLUMN_IS_RECURRING, if (transaction.isrecurring) 1 else 0)
            put(TransactionSchema.COLUMN_TYPE, transaction.type)
            put(TransactionSchema.COLUMN_CATEGORY_ID, transaction.categoryId)
            put(TransactionSchema.COLUMN_SYNC_STATUS, 0)
        }
        val result = db.insert(TransactionSchema.TABLE_NAME, null, values)
        db.close()
        return result != -1L
    }

    // Transaction Management
    fun addTransactionSync(transaction: Transaction): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(TransactionSchema.COLUMN_ID, transaction.id)
            put(TransactionSchema.COLUMN_NAME, transaction.name)
            put(TransactionSchema.COLUMN_AMOUNT, transaction.amount)
            put(TransactionSchema.COLUMN_DATE, transaction.date)
            put(TransactionSchema.COLUMN_USER_ID, transaction.userid)
            put(TransactionSchema.COLUMN_IS_RECURRING, if (transaction.isrecurring) 1 else 0)
            put(TransactionSchema.COLUMN_TYPE, transaction.type)
            put(TransactionSchema.COLUMN_CATEGORY_ID, transaction.categoryId)
            put(TransactionSchema.COLUMN_SYNC_STATUS, 1)
        }
        val result = db.insert(TransactionSchema.TABLE_NAME, null, values)
        db.close()
        return result != -1L
    }

    fun getTransaction(id: String): Transaction? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TransactionSchema.TABLE_NAME,
            null,
            "${TransactionSchema.COLUMN_ID} = ?",
            arrayOf(id),
            null,
            null,
            null
        )

        return if (cursor != null && cursor.moveToFirst()) {
            val transaction = Transaction(
                id = cursor.getString(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_NAME)),
                amount = cursor.getDouble(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_AMOUNT)),
                date = cursor.getLong(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_DATE)),
                userid = cursor.getString(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_USER_ID)),
                isrecurring = cursor.getInt(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_IS_RECURRING)) == 1,
                type = cursor.getString(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_TYPE)),
                categoryId = cursor.getString(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_CATEGORY_ID))
            )
            cursor.close()
            transaction
        } else {
            cursor?.close()
            null
        }
    }

    fun getAllTransactions(): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val db = dbHelper.readableDatabase
        val selectQuery = "SELECT * FROM ${TransactionSchema.TABLE_NAME}"
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val transaction = Transaction(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_NAME)),
                    amount = cursor.getDouble(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_AMOUNT)),
                    date = cursor.getLong(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_DATE)),
                    userid = cursor.getString(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_USER_ID)),
                    isrecurring = cursor.getInt(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_IS_RECURRING)) == 1,
                    type = cursor.getString(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_TYPE)),
                    categoryId = cursor.getString(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_CATEGORY_ID))
                )
                transactions.add(transaction)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return transactions
    }

    fun updateTransaction(transaction: Transaction): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(TransactionSchema.COLUMN_NAME, transaction.name)
            put(TransactionSchema.COLUMN_AMOUNT, transaction.amount)
            put(TransactionSchema.COLUMN_DATE, transaction.date)
            put(TransactionSchema.COLUMN_USER_ID, transaction.userid)
            put(TransactionSchema.COLUMN_IS_RECURRING, if (transaction.isrecurring) 1 else 0)
            put(TransactionSchema.COLUMN_TYPE, transaction.type)
            put(TransactionSchema.COLUMN_CATEGORY_ID, transaction.categoryId)
            put(TransactionSchema.COLUMN_SYNC_STATUS, 0)
        }
        val result = db.update(
            TransactionSchema.TABLE_NAME,
            values,
            "${TransactionSchema.COLUMN_ID} = ?",
            arrayOf(transaction.id)
        )
        db.close()
        return result > 0
    }

    fun deleteTransaction(id: String): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete(
            TransactionSchema.TABLE_NAME,
            "${TransactionSchema.COLUMN_ID} = ?",
            arrayOf(id)
        )
        db.close()
        return result > 0
    }

    // Sync Management
    fun getUnSyncedTransactions(): List<Transaction> {
        val unSyncedList = mutableListOf<Transaction>()
        val db = dbHelper.readableDatabase
        val selectQuery = "SELECT * FROM ${TransactionSchema.TABLE_NAME} WHERE ${TransactionSchema.COLUMN_SYNC_STATUS} = 0"
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val transaction = Transaction(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_NAME)),
                    amount = cursor.getDouble(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_AMOUNT)),
                    date = cursor.getLong(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_DATE)),
                    userid = cursor.getString(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_USER_ID)),
                    isrecurring = cursor.getInt(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_IS_RECURRING)) == 1,
                    type = cursor.getString(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_TYPE)),
                    categoryId = cursor.getString(cursor.getColumnIndexOrThrow(TransactionSchema.COLUMN_CATEGORY_ID))
                )
                unSyncedList.add(transaction)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return unSyncedList
    }

    fun updateCategoryId(localId: String, firebaseId: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(TransactionSchema.COLUMN_CATEGORY_ID, firebaseId) // Update local ID with Firebase ID
        }
        val result = db.update(
            TransactionSchema.TABLE_NAME,
            values,
            "${TransactionSchema.COLUMN_CATEGORY_ID} = ?",
            arrayOf(localId)
        )
        db.close()
        return result > 0
    }

    fun updateTransactionId(localId: String, firebaseId: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(TransactionSchema.COLUMN_ID, firebaseId) // Update local ID with Firebase ID
        }
        val result = db.update(
            TransactionSchema.TABLE_NAME,
            values,
            "${TransactionSchema.COLUMN_ID} = ?",
            arrayOf(localId)
        )
        db.close()
        return result > 0
    }

    fun markAsSynced(goalId: String) {
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(TransactionSchema.COLUMN_SYNC_STATUS, 1)  // Mark as synced
        }
        db.update(TransactionSchema.TABLE_NAME, contentValues, "${TransactionSchema.COLUMN_ID} = ?", arrayOf(goalId))
        db.close()
    }

    fun markCategoryForDeletion(goalId: String): Int {
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(TransactionSchema.COLUMN_SYNC_STATUS, -1)  // Mark for deletion
        }
        val result = db.update(TransactionSchema.TABLE_NAME, contentValues, "${TransactionSchema.COLUMN_ID} = ?", arrayOf(goalId))
        db.close()
        return result
    }
}
