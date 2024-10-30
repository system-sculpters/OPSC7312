package com.opsc.opsc7312.view.observers

import android.util.Log
import androidx.lifecycle.Observer
import com.opsc.opsc7312.model.data.model.Notification
import com.opsc.opsc7312.view.adapter.NotificationAdapter

class NotificationsObserver(private val adapter: NotificationAdapter): Observer<List<Notification>> {
    // This class was adapted from stackoverflow
    // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
    // Kevin Robatel
    // https://stackoverflow.com/users/244702/kevin-robatel
    // Method called when the observed data changes
    override fun onChanged(value: List<Notification>) {
        // Update the data in the CategoryListAdapter
        adapter.updateNotifications(value)


        Log.d("Notifications", "notifications retrieved: $value")
    }
}