package com.opsc.opsc7312.model.data.offline.dbhelpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.opsc.opsc7312.model.data.offline.schema.CategorySchema
import com.opsc.opsc7312.model.data.offline.schema.GoalSchema
import com.opsc.opsc7312.model.data.offline.schema.TransactionSchema

class DatabaseHelperProvider(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "pennywise.db"
        private const val DATABASE_VERSION = 2
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create tables when the database is created
        db.execSQL(GoalSchema.CREATE_TABLE)
        db.execSQL(CategorySchema.CREATE_TABLE)
        db.execSQL(TransactionSchema.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades
        db.execSQL("DROP TABLE IF EXISTS ${TransactionSchema.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${CategorySchema.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${GoalSchema.TABLE_NAME}")
        onCreate(db) // Recreate the database
    }
}