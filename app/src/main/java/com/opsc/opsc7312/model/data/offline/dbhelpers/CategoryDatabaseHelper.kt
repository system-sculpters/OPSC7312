package com.opsc.opsc7312.model.data.offline.dbhelpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.opsc.opsc7312.model.data.model.Category
import com.opsc.opsc7312.model.data.offline.DatabaseChangeListener
import com.opsc.opsc7312.model.data.offline.schema.CategorySchema
import com.opsc.opsc7312.model.data.offline.schema.GoalSchema

class CategoryDatabaseHelper (context: Context) {
    // This class was adapted from geeksforgeeks
    // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
    // scoder13
    // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
    private val dbHelper = DatabaseHelperProvider(context)

    private var changeListener: DatabaseChangeListener? = null

    fun setDatabaseChangeListener(listener: DatabaseChangeListener?) {
        this.changeListener = listener
    }
    // Method to insert a new category into the database
    fun insertCategory(category: Category): Long {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(CategorySchema.COLUMN_ID, category.id)
            put(CategorySchema.COLUMN_NAME, category.name)
            put(CategorySchema.COLUMN_COLOR, category.color)
            put(CategorySchema.COLUMN_ICON, category.icon)
            put(CategorySchema.COLUMN_TRANSACTION_TYPE, category.transactiontype)
            put(CategorySchema.COLUMN_USER_ID, category.userid)
            put(CategorySchema.COLUMN_SYNC_STATUS, 0) // Mark as unsynced
        }
        val id = db.insert(CategorySchema.TABLE_NAME, null, contentValues)
        db.close()

        //changeListener?.onDatabaseChanged()

        return id
    }

    fun insertCategorySync(category: Category): Long {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(CategorySchema.COLUMN_ID, category.id)
            put(CategorySchema.COLUMN_NAME, category.name)
            put(CategorySchema.COLUMN_COLOR, category.color)
            put(CategorySchema.COLUMN_ICON, category.icon)
            put(CategorySchema.COLUMN_TRANSACTION_TYPE, category.transactiontype)
            put(CategorySchema.COLUMN_USER_ID, category.userid)
            put(CategorySchema.COLUMN_SYNC_STATUS, 1) // Mark as unsynced
        }
        val id = db.insert(CategorySchema.TABLE_NAME, null, contentValues)
        db.close()
        return id
    }

    // Method to update an existing category
    fun updateCategory(category: Category): Int {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(CategorySchema.COLUMN_NAME, category.name)
            put(CategorySchema.COLUMN_COLOR, category.color)
            put(CategorySchema.COLUMN_ICON, category.icon)
            put(CategorySchema.COLUMN_TRANSACTION_TYPE, category.transactiontype)
            put(CategorySchema.COLUMN_USER_ID, category.userid)
            put(CategorySchema.COLUMN_SYNC_STATUS, 0) // Mark as unsynced
        }
        val rowsAffected = db.update(CategorySchema.TABLE_NAME, contentValues, "${CategorySchema.COLUMN_ID} = ?", arrayOf(category.id))
        db.close()
        return rowsAffected
    }

    // Method to get all categories for a specific user from the database
    fun getAllCategories(userId: String): List<Category> {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val categoryList = mutableListOf<Category>()
        val db = dbHelper.readableDatabase

        // Update the query to include a WHERE clause to filter by userId
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM ${CategorySchema.TABLE_NAME} WHERE ${CategorySchema.COLUMN_USER_ID} = ?",
            arrayOf(userId)
        )

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
                categoryList.add(category)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return categoryList
    }

    // method to get category by id
    fun getCategoryById(categoryId: String): Category? {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val db = dbHelper.readableDatabase
        var category: Category? = null
        val cursor = db.rawQuery("SELECT * FROM ${CategorySchema.TABLE_NAME} WHERE ${CategorySchema.COLUMN_ID} = ?", arrayOf(categoryId))

        cursor.use {
            if (it.moveToFirst()) {
                category = Category(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(CategorySchema.COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(CategorySchema.COLUMN_NAME)),
                    color = cursor.getString(cursor.getColumnIndexOrThrow(CategorySchema.COLUMN_COLOR)),
                    icon = cursor.getString(cursor.getColumnIndexOrThrow(CategorySchema.COLUMN_ICON)),
                    transactiontype = cursor.getString(cursor.getColumnIndexOrThrow(CategorySchema.COLUMN_TRANSACTION_TYPE)),
                    userid = cursor.getString(cursor.getColumnIndexOrThrow(CategorySchema.COLUMN_USER_ID))
                )
            }
        }
        db.close()
        return category
    }

    fun updateCategoryId(localId: String, firebaseId: String): Boolean {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(CategorySchema.COLUMN_ID, firebaseId) // Update local ID with Firebase ID
        }
        val result = db.update(
            CategorySchema.TABLE_NAME,
            values,
            "${CategorySchema.COLUMN_ID} = ?",
            arrayOf(localId)
        )
        db.close()
        return result > 0
    }

    // Method to get unsynced categories for a specific user
    fun getUnSyncedCategories(userId: String): List<Category> {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val unSyncedList = mutableListOf<Category>()
        val db = dbHelper.readableDatabase

        // Update the query to include both SYNC_STATUS and userId in the WHERE clause
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM ${CategorySchema.TABLE_NAME} WHERE ${CategorySchema.COLUMN_SYNC_STATUS} = 0 AND ${CategorySchema.COLUMN_USER_ID} = ?",
            arrayOf(userId)
        )

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


    // Method to mark a category as synced
    fun markAsSynced(categoryId: String) {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(CategorySchema.COLUMN_SYNC_STATUS, 1) // Mark as synced
        }
        db.update(CategorySchema.TABLE_NAME, contentValues, "${CategorySchema.COLUMN_ID} = ?", arrayOf(categoryId))
        db.close()
    }

    // Method to mark a category for deletion
    fun markCategoryForDeletion(categoryId: String): Int {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(CategorySchema.COLUMN_SYNC_STATUS, -1) // Mark for deletion
        }
        val result = db.update(CategorySchema.TABLE_NAME, contentValues, "${CategorySchema.COLUMN_ID} = ?", arrayOf(categoryId))
        db.close()
        return result
    }

    // Method to delete a category by its ID
    fun deleteCategory(categoryId: String): Int {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val db = dbHelper.writableDatabase
        val result = db.delete(CategorySchema.TABLE_NAME, "${CategorySchema.COLUMN_ID} = ?", arrayOf(categoryId))
        db.close()
        return result
    }
}