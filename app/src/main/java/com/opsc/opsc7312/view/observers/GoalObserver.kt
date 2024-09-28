package com.opsc.opsc7312.view.observers

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Observer
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.model.data.model.Goal
import com.opsc.opsc7312.view.adapter.GoalAdapter

// This class observes changes to a list of goals and updates the UI accordingly.
// Adapted from:
// https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
// Author: Kevin Robatel

// This class observes changes to a list of goals and updates the UI accordingly.
class GoalObserver(
    private val adapter: GoalAdapter?,
    private val amount: TextView
) : Observer<List<Goal>> {

    // Invoked when the observed list of goals changes.
    override fun onChanged(value: List<Goal>) {
        // Refresh the GoalAdapter with the latest goals.
        adapter?.updateGoals(value)

        // Call the method to update the displayed progress of the goals.
        updateGoalProgress(value)

        // Log the number of goals retrieved for debugging purposes.
        Log.d("Category", "Category retrieved: ${value.size}\n $value")
    }

    // Calculates and displays the total progress of the goals.
    private fun updateGoalProgress(goals: List<Goal>) {
        var totalCurrentAmount = 0.0  // Initialize total for current amounts.
        var totalTargetAmount = 0.0    // Initialize total for target amounts.

        // Iterate through each goal to accumulate the current and target amounts.
        for (goal in goals) {
            totalCurrentAmount += goal.currentamount
            totalTargetAmount += goal.targetamount
        }

        // Update the TextView with the formatted amounts reflecting progress.
        amount.text = "${AppConstants.formatAmount(totalCurrentAmount)}/" +
                "${AppConstants.formatAmount(totalTargetAmount)} ZAR"
    }
}
