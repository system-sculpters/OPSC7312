package com.opsc.opsc7312.model.data.offline.dbhelpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.opsc.opsc7312.model.data.model.Goal
import com.opsc.opsc7312.model.data.offline.schema.GoalSchema

class GoalDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "pennywise.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableStatement = """
            CREATE TABLE ${GoalSchema.TABLE_NAME} (
                ${GoalSchema.COLUMN_ID} TEXT PRIMARY KEY,
                ${GoalSchema.COLUMN_USER_ID} TEXT,
                ${GoalSchema.COLUMN_NAME} TEXT,
                ${GoalSchema.COLUMN_TARGET_AMOUNT} REAL,
                ${GoalSchema.COLUMN_CURRENT_AMOUNT} REAL,
                ${GoalSchema.COLUMN_DEADLINE} INTEGER,
                ${GoalSchema.COLUMN_CONTRIBUTION_TYPE} TEXT,
                ${GoalSchema.COLUMN_CONTRIBUTION_AMOUNT} REAL,
                ${GoalSchema.COLUMN_SYNC_STATUS} INTEGER
            )
        """
        db.execSQL(createTableStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Add missing 'contributionamount' column
            db.execSQL("ALTER TABLE ${GoalSchema.TABLE_NAME} ADD COLUMN ${GoalSchema.COLUMN_CONTRIBUTION_AMOUNT} REAL")
        }
    }


    fun insertGoal(goal: Goal): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(GoalSchema.COLUMN_ID, goal.id)
            put(GoalSchema.COLUMN_USER_ID, goal.userid)
            put(GoalSchema.COLUMN_NAME, goal.name)
            put(GoalSchema.COLUMN_TARGET_AMOUNT, goal.targetamount)
            put(GoalSchema.COLUMN_CURRENT_AMOUNT, goal.currentamount)
            put(GoalSchema.COLUMN_DEADLINE, goal.deadline)
            put(GoalSchema.COLUMN_CONTRIBUTION_TYPE, goal.contributiontype)
            put(GoalSchema.COLUMN_CONTRIBUTION_AMOUNT, goal.contributionamount)
            put(GoalSchema.COLUMN_SYNC_STATUS, 0)
        }
        val result = db.insert(GoalSchema.TABLE_NAME, null, values)
        db.close()
        return result != -1L
    }

    fun getGoal(id: String): Goal? {
        val db = readableDatabase
        val cursor = db.query(
            GoalSchema.TABLE_NAME,
            null,
            "${GoalSchema.COLUMN_ID} = ?",
            arrayOf(id),
            null,
            null,
            null
        )

        return if (cursor != null && cursor.moveToFirst()) {
            val goal = Goal(
                id = cursor.getString(cursor.getColumnIndexOrThrow(GoalSchema.COLUMN_ID)),
                userid = cursor.getString(cursor.getColumnIndexOrThrow(GoalSchema.COLUMN_USER_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(GoalSchema.COLUMN_NAME)),
                targetamount = cursor.getDouble(cursor.getColumnIndexOrThrow(GoalSchema.COLUMN_TARGET_AMOUNT)),
                currentamount = cursor.getDouble(cursor.getColumnIndexOrThrow(GoalSchema.COLUMN_CURRENT_AMOUNT)),
                deadline = cursor.getLong(cursor.getColumnIndexOrThrow(GoalSchema.COLUMN_DEADLINE)),
                contributiontype = cursor.getString(cursor.getColumnIndexOrThrow(GoalSchema.COLUMN_CONTRIBUTION_TYPE)),
                contributionamount = cursor.getDouble(cursor.getColumnIndexOrThrow(GoalSchema.COLUMN_CONTRIBUTION_AMOUNT))
            )
            cursor.close()
            goal
        } else {
            cursor?.close()
            null
        }
    }

    fun getAllGoals(): List<Goal> {
        val goals = mutableListOf<Goal>()
        val db = readableDatabase
        val cursor = db.query(
            GoalSchema.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "${GoalSchema.COLUMN_DEADLINE} ASC"
        )

        with(cursor) {
            while (this != null && moveToNext()) {
                val goal = Goal(
                    id = getString(getColumnIndexOrThrow(GoalSchema.COLUMN_ID)),
                    userid = getString(getColumnIndexOrThrow(GoalSchema.COLUMN_USER_ID)),
                    name = getString(getColumnIndexOrThrow(GoalSchema.COLUMN_NAME)),
                    targetamount = getDouble(getColumnIndexOrThrow(GoalSchema.COLUMN_TARGET_AMOUNT)),
                    currentamount = getDouble(getColumnIndexOrThrow(GoalSchema.COLUMN_CURRENT_AMOUNT)),
                    deadline = getLong(getColumnIndexOrThrow(GoalSchema.COLUMN_DEADLINE)),
                    contributiontype = getString(getColumnIndexOrThrow(GoalSchema.COLUMN_CONTRIBUTION_TYPE)),
                    contributionamount = getDouble(getColumnIndexOrThrow(GoalSchema.COLUMN_CONTRIBUTION_AMOUNT))
                )
                goals.add(goal)
            }
            close()
        }
        return goals
    }

    fun updateGoal(goal: Goal): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(GoalSchema.COLUMN_USER_ID, goal.userid)
            put(GoalSchema.COLUMN_NAME, goal.name)
            put(GoalSchema.COLUMN_TARGET_AMOUNT, goal.targetamount)
            put(GoalSchema.COLUMN_CURRENT_AMOUNT, goal.currentamount)
            put(GoalSchema.COLUMN_DEADLINE, goal.deadline)
            put(GoalSchema.COLUMN_CONTRIBUTION_TYPE, goal.contributiontype)
            put(GoalSchema.COLUMN_CONTRIBUTION_AMOUNT, goal.contributionamount)
            put(GoalSchema.COLUMN_SYNC_STATUS, 0)
        }
        val result = db.update(
            GoalSchema.TABLE_NAME,
            values,
            "${GoalSchema.COLUMN_ID} = ?",
            arrayOf(goal.id)
        )
        db.close()
        return result > 0
    }

    fun getUnSyncedGoals(): List<Goal> {
        val unSyncedList = mutableListOf<Goal>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM ${GoalSchema.TABLE_NAME} WHERE ${GoalSchema.COLUMN_SYNC_STATUS} = 0"
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            with(cursor){
                do {
                    val goal = Goal(
                        id = getString(getColumnIndexOrThrow(GoalSchema.COLUMN_ID)),
                        userid = getString(getColumnIndexOrThrow(GoalSchema.COLUMN_USER_ID)),
                        name = getString(getColumnIndexOrThrow(GoalSchema.COLUMN_NAME)),
                        targetamount = getDouble(getColumnIndexOrThrow(GoalSchema.COLUMN_TARGET_AMOUNT)),
                        currentamount = getDouble(getColumnIndexOrThrow(GoalSchema.COLUMN_CURRENT_AMOUNT)),
                        deadline = getLong(getColumnIndexOrThrow(GoalSchema.COLUMN_DEADLINE)),
                        contributiontype = getString(getColumnIndexOrThrow(GoalSchema.COLUMN_CONTRIBUTION_TYPE)),
                        contributionamount = getDouble(getColumnIndexOrThrow(GoalSchema.COLUMN_CONTRIBUTION_AMOUNT))
                    )
                    unSyncedList.add(goal)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        db.close()
        return unSyncedList
    }

    fun markAsSynced(goalId: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(GoalSchema.COLUMN_SYNC_STATUS, 1)  // Mark as synced
        }
        db.update(GoalSchema.TABLE_NAME, contentValues, "${GoalSchema.COLUMN_ID} = ?", arrayOf(goalId))
        db.close()
    }

    fun updateGoalId(localId: String, firebaseId: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(GoalSchema.COLUMN_ID, firebaseId) // Update local ID with Firebase ID
        }
        val result = db.update(
            GoalSchema.TABLE_NAME,
            values,
            "${GoalSchema.COLUMN_ID} = ?",
            arrayOf(localId)
        )
        db.close()
        return result > 0
    }


    fun markGoalForDeletion(goalId: String): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(GoalSchema.COLUMN_SYNC_STATUS, -1)  // Mark for deletion
        }
        val result = db.update(GoalSchema.TABLE_NAME, contentValues, "${GoalSchema.COLUMN_ID} = ?", arrayOf(goalId))
        db.close()
        return result
    }


    fun deleteGoal(id: String): Boolean {
        val db = writableDatabase
        val result = db.delete(
            GoalSchema.TABLE_NAME,
            "${GoalSchema.COLUMN_ID} = ?",
            arrayOf(id)
        )
        db.close()
        return result > 0
    }
}
