package com.opsc.opsc7312.view.custom

import android.content.Context
import android.net.ConnectivityManager

class NetworkManager {
    // This class was adapted from medium
    // https://medium.com/@dilipsuthar97/listen-to-internet-connection-using-broadcastreceiver-in-android-kotlin-6b527426a6f2
    // Dilip Suthar
    // https://medium.com/@dilipsuthar97
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}