package com.opsc.opsc7312.model.data.offline.syncworker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.opsc.opsc7312.model.api.controllers.GoalController
import com.opsc.opsc7312.model.data.model.Goal
import com.opsc.opsc7312.model.data.model.IdMapping
import com.opsc.opsc7312.model.data.offline.dbhelpers.GoalDatabaseHelper
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager

class GoalSyncWorker (private  val appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val goalDbHelper = GoalDatabaseHelper(applicationContext)
        val tokenManager = TokenManager.getInstance(applicationContext)

        // Get the unSynced categories
        val unSyncedGoals = goalDbHelper.getUnSyncedGoals()

        if (unSyncedGoals.isNotEmpty()) {
            // Get the token
            val token = tokenManager.getToken()
            if (token != null) {
                // Create an instance of the CategoryController for syncing
                val goalController = GoalController()

                // Trigger sync directly
                goalController.syncGoals(token, unSyncedGoals){ status, ids ->
                    Log.d("ids", "these are the ids: $ids")
                    if (status) {
                        markAsSynced(unSyncedGoals, goalDbHelper) // Mark goals as synced
                        updateGoalIds(ids, goalDbHelper)
                    } else {
                        Log.e("SyncWorker", "Sync failed")
                    }
                }


                //return Result.retry() // Returning Result.retry() to allow retry in case of failure

                // Consider waiting for the sync to complete if needed
                // This might require some additional logic to wait for response
                // or set a flag indicating sync is in progress
            } else {
                Log.d("SyncWorker", "User logged out")
                return Result.failure()
            }
        }

        return Result.success()
    }

    private fun updateGoalIds(ids: List<IdMapping>?, dbHelper: GoalDatabaseHelper) {
        if (ids != null) {
            for (id in ids){
                dbHelper.updateGoalId(id.localId, id.firebaseId)
            }
        }
    }

    private fun markAsSynced(unSyncedGoals: List<Goal>, dbHelper: GoalDatabaseHelper) {
        unSyncedGoals.forEach { goal ->
            dbHelper.markAsSynced(goal.id) // Assuming Goal has a method to get its ID
        }
    }
}