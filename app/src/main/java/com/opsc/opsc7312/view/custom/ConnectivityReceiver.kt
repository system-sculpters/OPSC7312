package com.opsc.opsc7312.view.custom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

// BroadcastReceiver to monitor network connectivity changes
class ConnectivityReceiver(private val onNetworkAvailable: () -> Unit) : BroadcastReceiver() {
    // This class was adapted from medium
    // https://medium.com/@dilipsuthar97/listen-to-internet-connection-using-broadcastreceiver-in-android-kotlin-6b527426a6f2
    // Dilip Suthar
    // https://medium.com/@dilipsuthar97

    // This method is triggered whenever the network connectivity changes
    override fun onReceive(context: Context, intent: Intent) {
        // Get the connectivity manager to check network status
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // Retrieve information about the active network
        val networkInfo = connectivityManager.activeNetworkInfo

        // Check if there is an active network and if it is connected
        if (networkInfo != null && networkInfo.isConnected) {
            // If connected, execute the callback function to handle network availability
            onNetworkAvailable() // This could be a sync action or any other network-dependent task
        }
    }
}
