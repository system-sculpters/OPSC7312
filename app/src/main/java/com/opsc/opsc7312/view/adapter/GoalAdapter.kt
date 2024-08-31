package com.opsc.opsc7312.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R
import com.opsc.opsc7312.model.data.model.Goal

class GoalAdapter (private val onItemClick: (Goal) -> Unit) :
    RecyclerView.Adapter<GoalAdapter.ViewHolder>(){

    // This class adapted from geeksforgeeks
    // https://www.geeksforgeeks.org/android-recyclerview/
    // BaibhavOjha
    // https://auth.geeksforgeeks.org/user/BaibhavOjha/articles?utm_source=geeksforgeeks&utm_medium=article_author&utm_campaign=auth_user

    private val goals = mutableListOf<Goal>()

    fun updateGoals(data: List<Goal>) {
        goals.clear()
        goals.addAll(data)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.goal_item_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val goal = goals[position]

        holder.goal_name.text = goal.name

        //holder.deadline.text = goal.deadline.toString()

        if(goal.deadline == 0L){
            holder.deadline.text = "No deadline"
        }
        else{
            holder.deadline.text = AppConstants.convertLongToString(goal.deadline)
        }

        Log.d("deadline", "this is the deadline ${goal.deadline}")


        holder.progress_amount.text = "${goal.currentamount}/${goal.targetamount} ZAR"

        val progress = if (goal.targetamount > 0) {
            (goal.currentamount / goal.targetamount * 100).toInt()
        } else {
            0
        }
        holder.progressBar.progress = progress

    }

    // Get item count
    override fun getItemCount(): Int {
        return goals.size
    }



    // View holder class
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val goal_name: TextView = itemView.findViewById(R.id.name)
        val deadline: TextView = itemView.findViewById(R.id.deadline)
        val progress_amount: TextView = itemView.findViewById(R.id.progress_amount)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

    }
}