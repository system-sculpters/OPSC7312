package com.opsc.opsc7312.view.custom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ConnectivityReceiver (private val onNetworkAvailable: () -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (NetworkManager().isNetworkAvailable(context)) {
            onNetworkAvailable()
        }
    }
}