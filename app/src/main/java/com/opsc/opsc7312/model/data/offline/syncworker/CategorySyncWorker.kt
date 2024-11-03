package com.opsc.opsc7312.model.data.offline.syncworker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.opsc.opsc7312.model.api.controllers.CategoryController
import com.opsc.opsc7312.model.data.model.Category
import com.opsc.opsc7312.model.data.model.IdMapping
import com.opsc.opsc7312.model.data.offline.dbhelpers.CategoryDatabaseHelper
import com.opsc.opsc7312.model.data.offline.dbhelpers.TransactionDatabaseHelper
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.custom.NotificationHandler
import java.util.concurrent.CompletableFuture

class CategorySyncWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    private var notificationHandler: NotificationHandler = NotificationHandler(applicationContext)

    override suspend fun doWork(): Result {
        val categoryDbHelper = CategoryDatabaseHelper(applicationContext)
        val tokenManager = TokenManager.getInstance(applicationContext)

        // Get the unsynced categories
        val unSyncedCategories = categoryDbHelper.getUnSyncedCategories()

        if (unSyncedCategories.isNotEmpty()) {
            // Get the token
            val token = tokenManager.getToken()
            if (token != null) {
                val categoryController = CategoryController()

                // Trigger sync directly with the suspend function
                val (status, ids) = categoryController.syncCategoriesSuspend(token, unSyncedCategories)
                Log.e("ids", "ids: $ids")
                if (status) {
                    markAsSynced(unSyncedCategories, categoryDbHelper)
                    updateCategoryIds(ids, categoryDbHelper)
                } else {
                    Log.e("SyncWorker", "Sync failed")
                    return Result.retry()
                }
            } else {
                Log.d("SyncWorker", "User logged out")
                return Result.failure()
            }
        }

        return syncRemoteToLocal(categoryDbHelper)
    }

    private suspend fun syncRemoteToLocal(categoryDbHelper: CategoryDatabaseHelper): Result {
        val token = TokenManager.getInstance(applicationContext).getToken()
        val user = UserManager.getInstance(applicationContext).getUser()

        return if (token != null) {
            val categoryController = CategoryController()

            // Get remote goals using a suspend function
            val remoteGoals = categoryController.getRemoteCategoriesSuspend(token, user.id)
            if (remoteGoals != null) {
                remoteGoals.forEach { remoteCategory ->
                    val localGoal = categoryDbHelper.getCategoryById(remoteCategory.id)
                    Log.d("localGoal", "localGoal ${localGoal}")
                    if (localGoal == null) {
                        Log.d("dbHelper.getAllCategories()", "localGoal ${categoryDbHelper.getAllCategories().find { id.toString() == remoteCategory.id }}")

                        categoryDbHelper.insertCategorySync(remoteCategory)
                    } else {
                        // Optionally update the local goal if needed
                    }
                }
                notificationHandler.showNotification("Sync Successful", "Categories synced successfully!")
                Result.success()
            } else {
                notificationHandler.showNotification("Sync Failed", "Failed to sync categories. Please check your internet connection.")
                Result.failure()
            }
        } else {
            Result.failure()
        }
    }


    private fun updateCategoryIds(ids: List<IdMapping>?, dbHelper: CategoryDatabaseHelper) {
        val transactionDatabaseHelper = TransactionDatabaseHelper(applicationContext)
        if (ids != null) {
            for (id in ids){
                dbHelper.updateCategoryId(id.localId, id.firebaseId)
                transactionDatabaseHelper.updateCategoryId(id.localId, id.firebaseId)
            }
        }

        Log.d("dbHelper.getAllCategories()", "categories ${dbHelper.getAllCategories()}")
        Log.d("getAllTransactions", "transactions ${transactionDatabaseHelper.getAllTransactions()}")
    }

    private fun markAsSynced(unSyncedCategories: List<Category>, dbHelper: CategoryDatabaseHelper) {
        unSyncedCategories.forEach { category ->
            dbHelper.markAsSynced(category.id) // Assuming Goal has a method to get its ID
        }
    }
}
