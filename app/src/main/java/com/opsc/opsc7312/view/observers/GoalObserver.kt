package com.opsc.opsc7312.view.observers

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Observer
import com.opsc.opsc7312.model.data.model.Goal
import com.opsc.opsc7312.view.adapter.GoalAdapter

class GoalObserver(
    private val adapter: GoalAdapter?, private val amount: TextView
): Observer<List<Goal>> {
    // This class was adapted from stackoverflow
    // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
    // Kevin Robatel
    // https://stackoverflow.com/users/244702/kevin-robatel

    // Method called when the observed data changes
    override fun onChanged(value: List<Goal>) {
        // Update the data in the CategoryListAdapter
        adapter?.updateGoals(value)

        // Update the categories in the associated ActivityFragment
        updateGoalProgress(value)

        Log.d("Category", "Category retrieved: ${value.size}\n $value")
    }

    private fun updateGoalProgress(goals: List<Goal>){
        var totalCurrentAmount = 0.0
        var totalTargetAmount = 0.0
        for(goal in goals){
            totalCurrentAmount += goal.currentamount
            totalTargetAmount += goal.targetamount
        }

        amount.text = "${totalCurrentAmount}/${totalTargetAmount} ZAR"
    }

}