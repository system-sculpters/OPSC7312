package com.opsc.opsc7312.model.data.offline.syncworker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.opsc.opsc7312.model.api.controllers.GoalController
import com.opsc.opsc7312.model.data.model.Goal
import com.opsc.opsc7312.model.data.model.IdMapping
import com.opsc.opsc7312.model.data.offline.dbhelpers.GoalDatabaseHelper
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager

class GoalSyncWorker (appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val goalDbHelper = GoalDatabaseHelper(applicationContext)
        val tokenManager = TokenManager.getInstance(applicationContext)

        // Get the unsynced goals
        val unSyncedGoals = goalDbHelper.getUnSyncedGoals()

        if (unSyncedGoals.isNotEmpty()) {
            // Get the token
            val token = tokenManager.getToken()
            if (token != null) {
                val goalController = GoalController()

                // Trigger sync directly with the suspend function
                val (status, ids) = goalController.syncGoalsSuspend(token, unSyncedGoals)
                Log.d("GoalSyncWorker", "Synced IDs: $ids")
                if (status) {
                    markAsSynced(unSyncedGoals, goalDbHelper)
                    updateGoalIds(ids, goalDbHelper)
                } else {
                    Log.e("GoalSyncWorker", "Sync failed")
                    return Result.retry()
                }
            } else {
                Log.d("GoalSyncWorker", "User logged out, no token available")
                return Result.failure()
            }
        }

        return syncRemoteToLocal(goalDbHelper)
    }

    private suspend fun syncRemoteToLocal(goalDbHelper: GoalDatabaseHelper): Result {
        val token = TokenManager.getInstance(applicationContext).getToken()
        val user = UserManager.getInstance(applicationContext).getUser()

        return if (token != null) {
            val goalController = GoalController()

            // Get remote goals using a suspend function
            val remoteGoals = goalController.getRemoteGoalsSuspend(token, user.id)
            if (remoteGoals != null) {
                remoteGoals.forEach { remoteGoal ->
                    val localGoal = goalDbHelper.getGoalById(remoteGoal.id)
                    Log.d("GoalSyncWorker", "Checking local goal with ID: ${remoteGoal.id}")
                    if (localGoal == null) {
                        goalDbHelper.insertGoalSync(remoteGoal)
                    } else {
                        // Optionally update the local goal if needed
                    }
                }
                Result.success()
            } else {
                Result.failure()
            }
        } else {
            Result.failure()
        }
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