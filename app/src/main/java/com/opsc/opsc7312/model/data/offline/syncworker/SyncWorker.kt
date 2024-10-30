package com.opsc.opsc7312.model.data.offline.syncworker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.opsc.opsc7312.model.api.controllers.CategoryController
import com.opsc.opsc7312.model.data.offline.dbhelpers.CategoryDatabaseHelper
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager

class SyncWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val categoryDbHelper = CategoryDatabaseHelper(applicationContext)
        val tokenManager = TokenManager.getInstance(applicationContext)

        // Get the unSynced categories
        val unSyncedCategories = categoryDbHelper.getUnSyncedCategories()

        if (unSyncedCategories.isNotEmpty()) {
            // Get the token
            val token = tokenManager.getToken()
            if (token != null) {
                // Create an instance of the CategoryController for syncing
                val categoryController = CategoryController()

                // Trigger sync directly
                categoryController.syncCategories(token, unSyncedCategories)

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
}
