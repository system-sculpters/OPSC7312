package com.opsc.opsc7312.model.data.offline.dbhelpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.opsc.opsc7312.model.data.model.Category
import com.opsc.opsc7312.model.data.offline.schema.CategorySchema

class CategoryDatabaseHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "pennywise.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE ${CategorySchema.TABLE_NAME} (" +
                "${CategorySchema.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${CategorySchema.COLUMN_NAME} TEXT, " +
                "${CategorySchema.COLUMN_COLOR} TEXT, " +
                "${CategorySchema.COLUMN_ICON} TEXT, " +
                "${CategorySchema.COLUMN_TRANSACTION_TYPE} TEXT, " +
                "${CategorySchema.COLUMN_USER_ID} TEXT, " +
                "${CategorySchema.COLUMN_SYNC_STATUS} INTEGER" +  // 0 = not synced, 1 = synced
                ")"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${CategorySchema.TABLE_NAME}")
        onCreate(db)
    }

    // Method to insert a new PIN into the database
    fun insertCategory(category: Category): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(CategorySchema.COLUMN_NAME, category.name)
            put(CategorySchema.COLUMN_COLOR, category.color)
            put(CategorySchema.COLUMN_ICON, category.icon)
            put(CategorySchema.COLUMN_TRANSACTION_TYPE, category.transactiontype)
            put(CategorySchema.COLUMN_USER_ID, category.userid)
            put(CategorySchema.COLUMN_SYNC_STATUS, 0)  // Mark as unsynced
        }
        return db.insert(CategorySchema.TABLE_NAME, null, contentValues)
    }

    fun updateCategory(category: Category): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(CategorySchema.COLUMN_NAME, category.name)
            put(CategorySchema.COLUMN_COLOR, category.color)
            put(CategorySchema.COLUMN_ICON, category.icon)
            put(CategorySchema.COLUMN_TRANSACTION_TYPE, category.transactiontype)
            put(CategorySchema.COLUMN_USER_ID, category.userid)
            put(CategorySchema.COLUMN_SYNC_STATUS, 0)  // Mark as unsynced
        }
        return db.update(CategorySchema.TABLE_NAME, contentValues, "${CategorySchema.COLUMN_ID} = ?", arrayOf(category.id))
    }


    // Method to get all PINs from the database
    fun getAllCategories(): List<Category> {
        val categoryList = mutableListOf<Category>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM ${CategorySchema.TABLE_NAME}"
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val category = Category(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(CategorySchema.COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(CategorySchema.COLUMN_NAME)),
                    color = cursor.getString(cursor.getColumnIndexOrThrow(CategorySchema.COLUMN_COLOR)),
                    icon = cursor.getString(cursor.getColumnIndexOrThrow(CategorySchema.COLUMN_ICON)),
                    transactiontype = cursor.getString(cursor.getColumnIndexOrThrow(CategorySchema.COLUMN_TRANSACTION_TYPE)),
                    userid = cursor.getString(cursor.getColumnIndexOrThrow(CategorySchema.COLUMN_USER_ID)),
                )
                categoryList.add(category)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return categoryList
    }

    fun getUnSyncedCategories(): List<Category> {
        val unSyncedList = mutableListOf<Category>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM ${CategorySchema.TABLE_NAME} WHERE ${CategorySchema.COLUMN_SYNC_STATUS} = 0"
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val category = Category(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(CategorySchema.COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(CategorySchema.COLUMN_NAME)),
                    color = cursor.getString(cursor.getColumnIndexOrThrow(CategorySchema.COLUMN_COLOR)),
                    icon = cursor.getString(cursor.getColumnIndexOrThrow(CategorySchema.COLUMN_ICON)),
                    transactiontype = cursor.getString(cursor.getColumnIndexOrThrow(CategorySchema.COLUMN_TRANSACTION_TYPE)),
                    userid = cursor.getString(cursor.getColumnIndexOrThrow(CategorySchema.COLUMN_USER_ID))
                )
                unSyncedList.add(category)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return unSyncedList
    }

    fun markAsSynced(categoryId: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(CategorySchema.COLUMN_SYNC_STATUS, 1)  // Mark as synced
        }
        db.update(CategorySchema.TABLE_NAME, contentValues, "${CategorySchema.COLUMN_ID} = ?", arrayOf(categoryId))
        db.close()
    }

    fun markCategoryForDeletion(categoryId: String): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(CategorySchema.COLUMN_SYNC_STATUS, -1)  // Mark for deletion
        }
        val result = db.update(CategorySchema.TABLE_NAME, contentValues, "${CategorySchema.COLUMN_ID} = ?", arrayOf(categoryId))
        db.close()
        return result
    }

    // Method to delete a PIN by its ID
    fun deleteCategory(categoryId: String): Int {
        val db = this.writableDatabase
        val result = db.delete(CategorySchema.TABLE_NAME, "${CategorySchema.COLUMN_ID} = ?", arrayOf(categoryId))
        db.close()
        return result
    }
}