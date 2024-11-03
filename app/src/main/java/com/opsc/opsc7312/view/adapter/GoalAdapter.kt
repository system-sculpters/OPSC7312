package com.opsc.opsc7312.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R
import com.opsc.opsc7312.model.data.model.Goal

// This class adapted from geeksforgeeks
// https://www.geeksforgeeks.org/android-recyclerview/
// BaibhavOjha
// https://auth.geeksforgeeks.org/user/BaibhavOjha/articles?utm_source=geeksforgeeks&utm_medium=article_author&utm_campaign=auth_user


class GoalAdapter(private val onItemClick: (Goal) -> Unit) :
    RecyclerView.Adapter<GoalAdapter.ViewHolder>() {

    // Mutable list to hold the goals
    private val goals = mutableListOf<Goal>()

    // Updates the list of goals and notifies the RecyclerView to refresh
    fun updateGoals(data: List<Goal>) {
        goals.clear() // Clear the existing goals
        goals.addAll(data) // Add new goals to the list
        notifyDataSetChanged() // Notify the adapter to refresh the view
    }

    // Creates a new ViewHolder for each goal item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the goal item layout and create a ViewHolder instance
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.goal_item_layout, parent, false)
        return ViewHolder(itemView) // Return the newly created ViewHolder
    }

    // Binds the data from the goals list to the ViewHolder at the specified position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val goal = goals[position] // Get the goal item for the current position

        // Set a click listener on the item view to trigger the onItemClick callback
        holder.itemView.setOnClickListener {
            onItemClick(goal) // Invoke the callback with the clicked goal
        }

        // Set the goal name in the corresponding TextView
        holder.goal_name.text = goal.name

        // Check the deadline and update the TextView accordingly
        if (goal.deadline == 0L) {
            holder.deadline.text = holder.itemView.context.getString(R.string.no_deadline) // Display "No deadline" if the deadline is 0
        } else {
            holder.deadline.text = AppConstants.convertLongToString(goal.deadline) // Convert and display the deadline
        }

        // Display the current amount versus the target amount
        holder.progress_amount.text = "${AppConstants.formatAmount(goal.currentamount)}/" +
                "${AppConstants.formatAmount(goal.targetamount)}"

        // Calculate and set the progress for the progress bar
        val progress = if (goal.targetamount > 0) {
            (goal.currentamount / goal.targetamount * 100).toInt() // Calculate progress percentage
        } else {
            0 // Set progress to 0 if target amount is 0
        }
        holder.progressBar.progress = progress // Update the progress bar with the calculated progress
    }

    // Returns the total number of goals in the list
    override fun getItemCount(): Int {
        return goals.size // Return the size of the goals list
    }

    // ViewHolder class for holding the views of each goal item
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val goal_name: TextView = itemView.findViewById(R.id.name) // TextView for the goal name
        val deadline: TextView = itemView.findViewById(R.id.deadline) // TextView for the goal deadline
        val progress_amount: TextView = itemView.findViewById(R.id.progress_amount) // TextView for displaying progress amount
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar) // ProgressBar for showing the progress of the goal
    }

}