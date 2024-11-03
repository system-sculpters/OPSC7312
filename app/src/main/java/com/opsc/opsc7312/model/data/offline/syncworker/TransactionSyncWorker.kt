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
    private var notificationHandler: NotificationHandler = NotificationHandler(applicationContext)
    override suspend fun doWork(): Result {
        val transactionDbHelper = TransactionDatabaseHelper(applicationContext)
        val tokenManager = TokenManager.getInstance(applicationContext)
        val userManager = UserManager.getInstance(applicationContext)

        val userId = userManager.getUser().id
        // Get the unsynced categories
        val unSyncedTransactions = transactionDbHelper.getUnSyncedTransactions(userId)

        if (unSyncedTransactions.isNotEmpty()) {
            // Get the token
            val token = tokenManager.getToken()
            if (token != null) {
                val transactionController = TransactionController()

                // Trigger sync directly with the suspend function
                val (status, ids) = transactionController.syncTransactionsSuspend(token, unSyncedTransactions)
                Log.e("ids", "ids: $ids")
                if (status) {
                    markAsSynced(unSyncedTransactions, transactionDbHelper)
                    updateCategoryIds(ids, transactionDbHelper, userId)
                } else {
                    Log.e("SyncWorker", "Sync failed")
                    return Result.retry()
                }
            } else {
                Log.d("SyncWorker", "User logged out")
                return Result.failure()
            }
        }

        return syncRemoteToLocal(transactionDbHelper, userId)
    }

    private suspend fun syncRemoteToLocal(transactionDbHelper: TransactionDatabaseHelper, userId: String): Result {
        val token = TokenManager.getInstance(applicationContext).getToken()
        val user = UserManager.getInstance(applicationContext).getUser()

        return if (token != null) {
            val transactionController = TransactionController()

            // Get remote goals using a suspend function
            val remoteTransactions = transactionController.getRemoteTransactionsSuspend(token, user.id)
            if (remoteTransactions != null) {
                remoteTransactions.forEach { remoteTransaction ->
                    val localGoal = transactionDbHelper.getTransaction(remoteTransaction.id)
                    Log.d("localGoal", "localGoal ${localGoal}")
                    if (localGoal == null) {
                        Log.d("dbHelper.getAllCategories()", "localGoal ${transactionDbHelper.getAllTransactions(userId).find { id.toString() == remoteTransaction.id }}")

                        transactionDbHelper.addTransactionSync(remoteTransaction)
                    } else {
                        // Optionally update the local goal if needed
                    }
                }
                notificationHandler.showNotification("Sync Successful", "Goals synced successfully!")

                Result.success()
            } else {
                notificationHandler.showNotification("Sync Failed", "Failed to sync categories. Please check your internet connection.")
                Result.failure()
            }
        } else {
            Result.failure()
        }
    }


    private fun updateCategoryIds(ids: List<IdMapping>?, dbHelper: TransactionDatabaseHelper, userId: String) {
        if (ids != null) {
            for (id in ids){
                dbHelper.updateTransactionId(id.localId, id.firebaseId)
            }
        }

        Log.d("getAllTransactions", "categories ${dbHelper.getAllTransactions(userId)}")
    }

    private fun markAsSynced(unSyncedTransactions: List<Transaction>, dbHelper: TransactionDatabaseHelper) {
        unSyncedTransactions.forEach { transaction ->
            dbHelper.markAsSynced(transaction.id) // Assuming Goal has a method to get its ID
        }
    }
}