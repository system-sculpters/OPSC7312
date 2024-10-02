package com.opsc.opsc7312.model.data.offline.dbhelpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.opsc.opsc7312.model.data.model.Category

class CategoryDatabaseHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "pennywise.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "category"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_COLOR = "color"
        const val COLUMN_ICON = "icon"
        const val COLUMN_TRANSACTION_TYPE = "transactiontype"
        const val COLUMN_USER_ID = "userid"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NAME TEXT, " +
                "$COLUMN_COLOR TEXT, " +
                "$COLUMN_ICON TEXT, " +
                "$COLUMN_TRANSACTION_TYPE TEXT, " +
                "$COLUMN_USER_ID TEXT" + ")"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Method to insert a new PIN into the database
    fun insertCategory(category: Category): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_ID, category.id)
            put(COLUMN_NAME, category.name)
            put(COLUMN_COLOR, category.color)
            put(COLUMN_ICON, category.icon)
            put(COLUMN_TRANSACTION_TYPE, category.transactiontype)
            put(COLUMN_USER_ID, category.userid)
        }
        return db.insert(TABLE_NAME, null, contentValues)
    }

    // Method to get all PINs from the database
    fun getAllCategories(): List<Category> {
        val categoryList = mutableListOf<Category>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val category = Category(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    color = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COLOR)),
                    icon = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ICON)),
                    transactiontype = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_TYPE)),
                    userid = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                )
                categoryList.add(category)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return categoryList
    }

    fun updateCategory(category: Category): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, category.name)
            put(COLUMN_COLOR, category.color)
            put(COLUMN_ICON, category.icon)
            put(COLUMN_TRANSACTION_TYPE, category.transactiontype)
            put(COLUMN_USER_ID, category.userid)
        }
        val result = db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(category.id))
        db.close()
        return result
    }


    // Method to delete a PIN by its ID
    fun deleteCategory(categoryId: String): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(categoryId))
        db.close()
        return result
    }
}