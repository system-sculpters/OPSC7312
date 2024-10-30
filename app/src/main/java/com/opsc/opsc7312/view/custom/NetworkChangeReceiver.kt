package com.opsc.opsc7312.view.custom

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.opsc.opsc7312.model.data.offline.syncworker.SyncWorker

class NetworkChangeReceiver(private val context: Context) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun registerNetworkCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val builder = NetworkRequest.Builder()
            connectivityManager.registerNetworkCallback(builder.build(), object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    // Trigger the sync operation when connected
                    startSyncWork()
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    // Handle network loss if needed
                }
            })
        }
    }


    private fun startSyncWork() {
        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .build()
        WorkManager.getInstance(context).enqueue(syncWorkRequest)
    }
}