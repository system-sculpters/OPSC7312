package com.opsc.opsc7312.model.data.offline.syncworker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.opsc.opsc7312.model.api.controllers.TransactionController
import com.opsc.opsc7312.model.data.model.IdMapping
import com.opsc.opsc7312.model.data.model.Transaction
import com.opsc.opsc7312.model.data.offline.dbhelpers.TransactionDatabaseHelper
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.custom.NotificationHandler

class TransactionSyncWorker (appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    // This class was adapted from medium
    // https://medium.com/@sedakundakitchen/exploring-coroutineworkers-an-introductory-guide-and-testing-approach-8b8987d9c2ac
    // Seda K
    // https://medium.com/@sedakundakitchen
    // Initialize the NotificationHandler to display notifications related to sync operations
    private var notificationHandler: NotificationHandler = NotificationHandler(applicationContext)

    override suspend fun doWork(): Result {
        // Helper to access transaction-related database operations
        val transactionDbHelper = TransactionDatabaseHelper(applicationContext)
        // Manager for handling user authentication tokens
        val tokenManager = TokenManager.getInstance(applicationContext)
        // Manager to retrieve user information
        val userManager = UserManager.getInstance(applicationContext)

        // Get the current user's ID
        val userId = userManager.getUser().id
        // Retrieve any unsynced transactions for the user
        val unSyncedTransactions = transactionDbHelper.getUnSyncedTransactions(userId)

        if (unSyncedTransactions.isNotEmpty()) {
            // Fetch the stored authentication token
            val token = tokenManager.getToken()
            if (token != null) {
                val transactionController = TransactionController()

                // Perform sync operation for unsynced transactions (suspend function for async operation)
                val (status, ids) = transactionController.syncTransactionsSuspend(token, unSyncedTransactions)
                Log.e("ids", "ids: $ids")

                // If sync was successful, mark transactions as synced and update category IDs
                if (status) {
                    markAsSynced(unSyncedTransactions, transactionDbHelper)
                    updateCategoryIds(ids, transactionDbHelper, userId)
                } else {
                    Log.e("SyncWorker", "Sync failed")
                    return Result.retry()  // Retry the work if sync fails
                }
            } else {
                Log.d("SyncWorker", "User logged out")
                return Result.failure()  // Mark as failed if no token is available (user is logged out)
            }
        }

        // Synchronize remote transactions with local database
        return syncRemoteToLocal(transactionDbHelper, userId)
    }

    private suspend fun syncRemoteToLocal(transactionDbHelper: TransactionDatabaseHelper, userId: String): Result {
        // Retrieve the authentication token and user details
        val token = TokenManager.getInstance(applicationContext).getToken()
        val user = UserManager.getInstance(applicationContext).getUser()

        return if (token != null) {
            val transactionController = TransactionController()

            // Retrieve remote transactions for the user
            val remoteTransactions = transactionController.getRemoteTransactionsSuspend(token, user.id)

            // If transactions were retrieved successfully
            if (remoteTransactions != null) {
                remoteTransactions.forEach { remoteTransaction ->
                    // Check if the transaction already exists locally
                    val localGoal = transactionDbHelper.getTransaction(remoteTransaction.id)
                    Log.d("localGoal", "localGoal $localGoal")

                    if (localGoal == null) {
                        // Add new transactions from remote data
                        transactionDbHelper.addTransactionSync(remoteTransaction)
                    } else {
                        // Optional: Update existing transaction if needed
                    }
                }
                // Show success notification to the user
                notificationHandler.showNotification("Sync Successful", "Goals synced successfully!")
                Result.success()  // Mark as successful if all transactions synced
            } else {
                // Show failure notification if remote sync fails
                notificationHandler.showNotification("Sync Failed", "Failed to sync categories. Please check your internet connection.")
                Result.failure()
            }
        } else {
            Result.failure()  // Mark as failed if no token is available
        }
    }


    // Updates local transaction IDs to match those on the server after sync
    private fun updateCategoryIds(ids: List<IdMapping>?, dbHelper: TransactionDatabaseHelper, userId: String) {
        if (ids != null) {
            for (id in ids) {
                dbHelper.updateTransactionId(id.localId, id.firebaseId)
            }
        }
        // Log the updated list of transactions for debugging
        Log.d("getAllTransactions", "categories ${dbHelper.getAllTransactions(userId)}")
    }

    // Marks a list of transactions as synced in the local database
    private fun markAsSynced(unSyncedTransactions: List<Transaction>, dbHelper: TransactionDatabaseHelper) {
        unSyncedTransactions.forEach { transaction ->
            dbHelper.markAsSynced(transaction.id)  // Assuming Transaction has a method to get its ID
        }
    }
}