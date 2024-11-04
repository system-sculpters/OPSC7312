package com.opsc.opsc7312.model.data.offline.dbhelpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.opsc.opsc7312.model.data.model.Category
import com.opsc.opsc7312.model.data.model.Goal
import com.opsc.opsc7312.model.data.offline.DatabaseChangeListener
import com.opsc.opsc7312.model.data.offline.schema.CategorySchema
import com.opsc.opsc7312.model.data.offline.schema.GoalSchema
import java.sql.SQLException

class GoalDatabaseHelper(context: Context){
    private val dbHelper = DatabaseHelperProvider(context)

    private var changeListener: DatabaseChangeListener? = null

    fun setDatabaseChangeListener(listener: DatabaseChangeListener?) {
        this.changeListener = listener
    }
    // Method to insert a new goal into the database
    fun insertGoal(goal: Goal): Long {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(GoalSchema.COLUMN_ID, goal.id)
            put(GoalSchema.COLUMN_USER_ID, goal.userid)
            put(GoalSchema.COLUMN_NAME, goal.name)
            put(GoalSchema.COLUMN_TARGET_AMOUNT, goal.targetamount)
            put(GoalSchema.COLUMN_CURRENT_AMOUNT, goal.currentamount)
            put(GoalSchema.COLUMN_DEADLINE, goal.deadline)
            put(GoalSchema.COLUMN_CONTRIBUTION_TYPE, goal.contributiontype)
            put(GoalSchema.COLUMN_CONTRIBUTION_AMOUNT, goal.contributionamount)
            put(GoalSchema.COLUMN_SYNC_STATUS, 0)  // Mark as unsynced
        }
        //changeListener?.onGoalsChanged()

        return db.insert(GoalSchema.TABLE_NAME, null, contentValues)
    }

    // Method to insert a new goal into the database
    fun insertGoalSync(goal: Goal): Long {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(GoalSchema.COLUMN_ID, goal.id)
            put(GoalSchema.COLUMN_USER_ID, goal.userid)
            put(GoalSchema.COLUMN_NAME, goal.name)
            put(GoalSchema.COLUMN_TARGET_AMOUNT, goal.targetamount)
            put(GoalSchema.COLUMN_CURRENT_AMOUNT, goal.currentamount)
            put(GoalSchema.COLUMN_DEADLINE, goal.deadline)
            put(GoalSchema.COLUMN_CONTRIBUTION_TYPE, goal.contributiontype)
            put(GoalSchema.COLUMN_CONTRIBUTION_AMOUNT, goal.contributionamount)
            put(GoalSchema.COLUMN_SYNC_STATUS, 1)  // Mark as unsynced
        }
        return db.insert(GoalSchema.TABLE_NAME, null, contentValues)
    }

    // Method to update an existing goal
    fun updateGoal(goal: Goal): Int {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(GoalSchema.COLUMN_NAME, goal.name)
            put(GoalSchema.COLUMN_TARGET_AMOUNT, goal.targetamount)
            put(GoalSchema.COLUMN_CURRENT_AMOUNT, goal.currentamount)
            put(GoalSchema.COLUMN_DEADLINE, goal.deadline)
            put(GoalSchema.COLUMN_CONTRIBUTION_TYPE, goal.contributiontype)
            put(GoalSchema.COLUMN_CONTRIBUTION_AMOUNT, goal.contributionamount)
            put(GoalSchema.COLUMN_SYNC_STATUS, 0)  // Mark as unsynced
        }
        return db.update(GoalSchema.TABLE_NAME, contentValues, "${GoalSchema.COLUMN_ID} = ?", arrayOf(goal.id))
    }

    // Method to get all goals from the database for a specific user
    fun getAllGoals(userId: String): List<Goal> {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val goalList = mutableListOf<Goal>()
        val db = dbHelper.readableDatabase
        // Include userId in the WHERE clause to filter goals by user
        val selectQuery = "SELECT * FROM ${GoalSchema.TABLE_NAME} WHERE ${GoalSchema.COLUMN_USER_ID} = ?"
        val cursor: Cursor = db.rawQuery(selectQuery, arrayOf(userId))

        if (cursor.moveToFirst()) {
            do {
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
                goalList.add(goal)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return goalList
    }

    // Method to get unsynced goals for a specific user
    fun getUnSyncedGoals(userId: String): List<Goal> {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val unSyncedList = mutableListOf<Goal>()
        val db = dbHelper.readableDatabase
        // Include userId in the WHERE clause to filter unsynced goals by user
        val selectQuery = "SELECT * FROM ${GoalSchema.TABLE_NAME} WHERE ${GoalSchema.COLUMN_SYNC_STATUS} = 0 AND ${GoalSchema.COLUMN_USER_ID} = ?"
        val cursor: Cursor = db.rawQuery(selectQuery, arrayOf(userId))

        if (cursor.moveToFirst()) {
            do {
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
                unSyncedList.add(goal)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return unSyncedList
    }


    // Method to mark a goal as synced
    fun markAsSynced(goalId: String) {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(GoalSchema.COLUMN_SYNC_STATUS, 1)  // Mark as synced
        }
        db.update(GoalSchema.TABLE_NAME, contentValues, "${GoalSchema.COLUMN_ID} = ?", arrayOf(goalId))
        db.close()
    }

    // method to update a goal
    fun updateGoalId(localId: String, firebaseId: String): Boolean {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val db = dbHelper.writableDatabase
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

    // method to get a goal by id
    fun getGoalById(goalId: String): Goal? {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val db = dbHelper.readableDatabase
        var goal: Goal? = null
        val cursor = db.rawQuery("SELECT * FROM ${GoalSchema.TABLE_NAME} WHERE ${CategorySchema.COLUMN_ID} = ?", arrayOf(goalId))

        cursor.use {
            if (it.moveToFirst()) {
                    goal = Goal(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(GoalSchema.COLUMN_ID)),
                    userid = cursor.getString(cursor.getColumnIndexOrThrow(GoalSchema.COLUMN_USER_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(GoalSchema.COLUMN_NAME)),
                    targetamount = cursor.getDouble(cursor.getColumnIndexOrThrow(GoalSchema.COLUMN_TARGET_AMOUNT)),
                    currentamount = cursor.getDouble(cursor.getColumnIndexOrThrow(GoalSchema.COLUMN_CURRENT_AMOUNT)),
                    deadline = cursor.getLong(cursor.getColumnIndexOrThrow(GoalSchema.COLUMN_DEADLINE)),
                    contributiontype = cursor.getString(cursor.getColumnIndexOrThrow(GoalSchema.COLUMN_CONTRIBUTION_TYPE)),
                    contributionamount = cursor.getDouble(cursor.getColumnIndexOrThrow(GoalSchema.COLUMN_CONTRIBUTION_AMOUNT))
                )
            }
        }
        db.close()
        return goal
    }
    // Method to mark a goal for deletion
    fun markGoalForDeletion(goalId: String): Int {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(GoalSchema.COLUMN_SYNC_STATUS, -1)  // Mark for deletion
        }
        val result = db.update(GoalSchema.TABLE_NAME, contentValues, "${GoalSchema.COLUMN_ID} = ?", arrayOf(goalId))
        db.close()
        return result
    }

    // Method to delete a goal by its ID
    fun deleteGoal(goalId: String): Int {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val db = dbHelper.writableDatabase
        val result = db.delete(GoalSchema.TABLE_NAME, "${GoalSchema.COLUMN_ID} = ?", arrayOf(goalId))
        db.close()
        return result
    }
}
