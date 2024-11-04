package com.opsc.opsc7312.model.data.offline.syncworker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.opsc.opsc7312.model.api.controllers.CategoryController
import com.opsc.opsc7312.model.data.model.Category
import com.opsc.opsc7312.model.data.model.IdMapping
import com.opsc.opsc7312.model.data.offline.dbhelpers.CategoryDatabaseHelper
import com.opsc.opsc7312.model.data.offline.dbhelpers.TransactionDatabaseHelper
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.custom.NotificationHandler

class CategorySyncWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    // This class was adapted from medium
    // https://medium.com/@sedakundakitchen/exploring-coroutineworkers-an-introductory-guide-and-testing-approach-8b8987d9c2ac
    // Seda K
    // https://medium.com/@sedakundakitchen

    // Initialize the NotificationHandler to manage notifications for sync status
    private var notificationHandler: NotificationHandler = NotificationHandler(applicationContext)

    // method to sync data from the local database to the remote database
    override suspend fun doWork(): Result {
        val categoryDbHelper = CategoryDatabaseHelper(applicationContext)
        val tokenManager = TokenManager.getInstance(applicationContext)

        val userManager = UserManager.getInstance(applicationContext)

        val userId = userManager.getUser().id
        // Get the unsynced categories
        val unSyncedCategories = categoryDbHelper.getUnSyncedCategories(userId)

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
                    updateCategoryIds(ids, categoryDbHelper, userId)
                } else {
                    Log.e("SyncWorker", "Sync failed")
                    return Result.retry()
                }
            } else {
                Log.d("SyncWorker", "User logged out")
                return Result.failure()
            }
        }

        return syncRemoteToLocal(categoryDbHelper, userId)
    }
    // method to sync data from the database to the local database
    private suspend fun syncRemoteToLocal(categoryDbHelper: CategoryDatabaseHelper, userId: String): Result {
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
                        Log.d("dbHelper.getAllCategories()", "localGoal ${categoryDbHelper.getAllCategories(userId).find { id.toString() == remoteCategory.id }}")

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


    // Updates local category IDs to match those on the server after sync
    private fun updateCategoryIds(
        ids: List<IdMapping>?,
        dbHelper: CategoryDatabaseHelper,
        userId: String
    ) {
        val transactionDatabaseHelper = TransactionDatabaseHelper(applicationContext)
        if (ids != null) {
            for (id in ids) {
                // Update category ID in both category and transaction databases
                dbHelper.updateCategoryId(id.localId, id.firebaseId)
                transactionDatabaseHelper.updateCategoryId(id.localId, id.firebaseId)
            }
        }
        // Log updated categories and transactions for debugging
        Log.d("dbHelper.getAllCategories()", "categories ${dbHelper.getAllCategories(userId)}")
        Log.d("getAllTransactions", "transactions ${transactionDatabaseHelper.getAllTransactions(userId)}")
    }

    // Marks a list of categories as synced in the local database
    private fun markAsSynced(unSyncedCategories: List<Category>, dbHelper: CategoryDatabaseHelper) {
        unSyncedCategories.forEach { category ->
            dbHelper.markAsSynced(category.id)  // Assuming Category has a method to get its ID
        }
    }
}
