package com.opsc.opsc7312.model.data.offline.dbhelpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.model.data.offline.DatabaseChangeListener
import com.opsc.opsc7312.model.data.offline.schema.CategorySchema
import com.opsc.opsc7312.model.data.offline.schema.TransactionSchema

class TransactionDatabaseHelper(context: Context){
    private val dbHelper = DatabaseHelperProvider(context)

    private var changeListener: DatabaseChangeListener? = null

    fun setDatabaseChangeListener(listener: DatabaseChangeListener?) {
        this.changeListener = listener
    }
    // method to add a new transaction
    fun addTransaction(transaction: Transaction): Boolean {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
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
        //changeListener?.onTransactionsChanged()

        return result != -1L
    }

    // method to add a transaction from the database
    fun addTransactionSync(transaction: Transaction): Boolean {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
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

    //method to retrieve a transaction by id
    fun getTransaction(id: String): Transaction? {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
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

    // method to retrieve all transactions
    fun getAllTransactions(userId: String): List<Transaction> {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val transactions = mutableListOf<Transaction>()
        val db = dbHelper.readableDatabase
        // Include userId in the WHERE clause to filter transactions by user
        val selectQuery = "SELECT * FROM ${TransactionSchema.TABLE_NAME} WHERE ${TransactionSchema.COLUMN_USER_ID} = ?"
        val cursor: Cursor = db.rawQuery(selectQuery, arrayOf(userId))

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
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
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

    fun getUnSyncedTransactions(userId: String): List<Transaction> {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val unSyncedList = mutableListOf<Transaction>()
        val db = dbHelper.readableDatabase
        // Update query to filter by both userId and sync status
        val selectQuery = "SELECT * FROM ${TransactionSchema.TABLE_NAME} WHERE ${TransactionSchema.COLUMN_SYNC_STATUS} = 0 AND ${TransactionSchema.COLUMN_USER_ID} = ?"
        val cursor: Cursor = db.rawQuery(selectQuery, arrayOf(userId))

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
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
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

    //method to update a transaction
    fun updateTransactionId(localId: String, firebaseId: String): Boolean {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
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

    // method to mark transaction as synced
    fun markAsSynced(goalId: String) {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(TransactionSchema.COLUMN_SYNC_STATUS, 1)  // Mark as synced
        }
        db.update(TransactionSchema.TABLE_NAME, contentValues, "${TransactionSchema.COLUMN_ID} = ?", arrayOf(goalId))
        db.close()
    }

    // method to mark transaction for deleting
    fun markTransactionForDeletion(goalId: String): Int {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(TransactionSchema.COLUMN_SYNC_STATUS, -1)  // Mark for deletion
        }
        val result = db.update(TransactionSchema.TABLE_NAME, contentValues, "${TransactionSchema.COLUMN_ID} = ?", arrayOf(goalId))
        db.close()
        return result
    }
}
