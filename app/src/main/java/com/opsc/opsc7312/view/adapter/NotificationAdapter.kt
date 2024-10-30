package com.opsc.opsc7312.view.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R
import com.opsc.opsc7312.model.data.model.Category
import com.opsc.opsc7312.model.data.model.Goal
import com.opsc.opsc7312.model.data.model.Notification
import com.opsc.opsc7312.view.adapter.GoalAdapter.ViewHolder
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale

// This class adapted from geeksforgeeks
// https://www.geeksforgeeks.org/android-recyclerview/
// BaibhavOjha
// https://auth.geeksforgeeks.org/user/BaibhavOjha/articles?utm_source=geeksforgeeks&utm_medium=article_author&utm_campaign=auth_user

class NotificationAdapter (private val onItemClick: (Notification) -> Unit) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>(){

        // Mutable list to hold the goals
        private val notifications = mutableListOf<Notification>()

        // Updates the list of goals and notifies the RecyclerView to refresh
        fun updateNotifications(data: List<Notification>) {
            notifications.clear() // Clear the existing goals
            notifications.addAll(data) // Add new goals to the list
            notifyDataSetChanged() // Notify the adapter to refresh the view
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            // Inflate the goal item layout and create a ViewHolder instance
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.notification_item_layout, parent, false)
            return ViewHolder(itemView) // Return the newly created ViewHolder
        }

        override fun getItemCount(): Int {
            return notifications.size // Return the number of categories in the list
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val currentItem = notifications[position] // Get the current category item

            holder.title.text = currentItem.title

            holder.message.text = currentItem.message

            // Convert createdAt timestamp to Date
            val date = Date(currentItem.createdAt)

            // Format date and time separately
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

            val formattedDate = dateFormatter.format(date)
            val formattedTime = timeFormatter.format(date)

            holder.time.text = formattedTime

            holder.itemView.setOnClickListener{
                onItemClick(currentItem)
            }
        }

        // ViewHolder class for holding the views of each goal item
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val title: TextView = itemView.findViewById(R.id.title) // TextView for the goal name
            val message: TextView = itemView.findViewById(R.id.message) // TextView for the goal deadline
            val time: TextView = itemView.findViewById(R.id.time) // TextView for displaying progress amount
        }
}