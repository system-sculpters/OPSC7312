package com.opsc.opsc7312.view.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable
import android.widget.LinearLayout

class CheckableLinearLayout @JvmOverloads constructor(
    context: Context, // Context in which the view is being created
    attrs: AttributeSet? = null, // Optional attribute set for XML inflation
    defStyleAttr: Int = 0 // Default style attribute
) : LinearLayout(context, attrs, defStyleAttr), Checkable { // Inherits from LinearLayout and implements Checkable interface

    // This class was adapted from stackoverflow
    // https://tutorial.eyehunts.com/android/android-linearlayout-tutorial-example-android-kotlin/
    // Rohit
    // https://tutorial.eyehunts.com/author/rohit/
    // Variable to hold the checked state of the layout
    private var isChecked = false

    // Returns the checked state of the layout
    override fun isChecked(): Boolean {
        return isChecked // Return the current checked state
    }

    // Sets the checked state of the layout and updates the UI if the state changes
    override fun setChecked(checked: Boolean) {
        // Only update if the new checked state is different from the current state
        if (isChecked != checked) {
            isChecked = checked // Update the checked state
            refreshDrawableState() // Refresh the drawable state to reflect the change
            // Update UI to show checked state, e.g., change background color or other visual indicators
        }
    }

    // Toggles the checked state of the layout and updates the UI accordingly
    override fun toggle() {
        isChecked = !isChecked // Flip the checked state
        refreshDrawableState() // Refresh the drawable state to reflect the change
        // Update UI to show toggled state
    }

    // Creates the drawable state for this view, incorporating the checked state
    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        // Get the drawable state from the parent class and add space for the checked state
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        // If the layout is checked, merge the checked state into the drawable state
        if (isChecked) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState // Return the modified drawable state
    }

    companion object {
        // Constant representing the checked state
        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    }
}
