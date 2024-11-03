package com.opsc.opsc7312.view.custom

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.opsc.opsc7312.R

class TimeOutDialog {
    // This class is responsible for displaying various types of dialogs related to timeout events
    // and progress updates. It includes methods to show a timeout alert dialog, a progress dialog,
    // and a custom alert dialog for error messages. The dialogs help provide feedback to the user
    // about the application's state during network operations.

    // Displays a timeout dialog to inform the user of a connection issue.
    // Provides options to either retry the connection or cancel the operation.
    fun showTimeoutDialog(context: Context, onRetry: () -> Unit) {
        // Build the alert dialog using the provided context.

        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/
        // naved_alam
        // https://www.geeksforgeeks.org/user/naved_alam/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val dialogBuilder = AlertDialog.Builder(context)

        // Set the message and configure the buttons for the dialog.
        dialogBuilder.setMessage(context.getString(R.string.connection_timeout))
            .setCancelable(false) // Prevent dialog from being canceled by tapping outside.
            .setPositiveButton(context.getString(R.string.retry)) { dialog, id ->
                // When "Retry" is clicked, invoke the onRetry callback and dismiss the dialog.
                dialog.dismiss()
                onRetry()

            }
            .setNegativeButton(context.getString(R.string.cancel_text)) { dialog, id ->
                // When "Cancel" is clicked, simply dismiss the dialog without any action.
                dialog.dismiss()
            }

        // Create the alert dialog instance and set its title.
        val alert = dialogBuilder.create()
        alert.setTitle(context.getString(R.string.connection_timeout_lbl))
        // Show the dialog to the user.
        alert.show()
    }

    // Shows a custom progress dialog that displays a loading indicator.
    // Returns the created AlertDialog instance for further manipulation if needed.
    fun showProgressDialog(context: Context): AlertDialog {
        // Inflate a custom layout for the progress dialog, which includes a progress bar.

        // This method was adapted from eeksforgeeks
        // https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/
        // naved_alam
        // https://www.geeksforgeeks.org/user/naved_alam/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val dialogView = LayoutInflater.from(context).inflate(R.layout.timeout_popup_dialog, null)

        // Create the AlertDialog using the custom layout.
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setView(dialogView) // Set the custom view for the dialog.
        dialogBuilder.setCancelable(false) // Prevent cancellation by tapping outside.

        // Create and display the dialog.
        val dialog = dialogBuilder.create()
        dialog.show()

        // Return the created dialog for potential future updates.
        return dialog
    }

    // Updates the content of the progress dialog based on the given parameters.
    // It updates the message, controls the visibility of the progress bar, and manages
    // the status indicator (checkmark or cross) based on the success or failure of the operation.
    fun updateProgressDialog(context: Context, dialog: AlertDialog, message: String, hideProgressBar: Boolean) {
        // Access UI elements from the dialog layout.

        // This method was adapted from eeksforgeeks
        // https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/
        // naved_alam
        // https://www.geeksforgeeks.org/user/naved_alam/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val progressBar = dialog.findViewById<ProgressBar>(R.id.progressBar)
        val checkmarkImageView = dialog.findViewById<ImageView>(R.id.checkmarkImageView)
        val statusTextView = dialog.findViewById<TextView>(R.id.statusTextView)

        // Update the status message displayed to the user.
        statusTextView?.text = message

        // Control the visibility of the progress bar based on the provided flag.
        if (hideProgressBar) {
            progressBar?.visibility = ProgressBar.GONE // Hide the progress bar if instructed.
        } else {
            progressBar?.visibility = ProgressBar.VISIBLE // Show the progress bar.
        }

        // Manage the visibility of the checkmark based on whether the progress bar is hidden.
        if (hideProgressBar) {
            // Check for failure messages to decide on the icon and color.
            if (message.lowercase().contains("fail") || message.lowercase().contains("unsuccessful")) {
                checkmarkImageView.setImageResource(R.drawable.ic_close) // Show a cross for failure.
                val color = ContextCompat.getColor(context, R.color.red) // Set color to red.
                checkmarkImageView.setColorFilter(color)
            } else {
                checkmarkImageView.setImageResource(R.drawable.baseline_check_green) // Show a checkmark for success.
                val color = ContextCompat.getColor(context, R.color.green) // Set color to green.
                checkmarkImageView.setColorFilter(color)
            }
            // Animate the appearance of the checkmark icon.
            val fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
            checkmarkImageView.startAnimation(fadeIn)
            checkmarkImageView?.visibility = ImageView.VISIBLE // Make the checkmark visible.
        } else {
            checkmarkImageView?.visibility = ImageView.GONE // Hide the checkmark if the progress bar is visible.
        }
    }

    // Displays a custom alert dialog to inform the user about an error with a specified message.
    // The dialog can be dismissed by the user.
    fun showAlertDialog(context: Context, errorMessage: String) {
        // Inflate a custom layout for the error dialog.
        // This method was adapted from eeksforgeeks
        // https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/
        // naved_alam
        // https://www.geeksforgeeks.org/user/naved_alam/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val dialogView = LayoutInflater.from(context).inflate(R.layout.input_error_dialog, null)

        // Access the message and dismiss button from the dialog layout.
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialogMessage)
        val dialogBtn = dialogView.findViewById<TextView>(R.id.dismissButton)

        // Set the error message text in the dialog.
        dialogMessage.text = errorMessage

        // Build the AlertDialog using the custom view.
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        // Set a click listener on the dismiss button to close the dialog.
        dialogBtn.setOnClickListener {
            dialogBuilder.dismiss() // Dismiss the dialog when the button is clicked.
        }

        // Show the alert dialog to the user.
        dialogBuilder.show()

        // Adjust the dialog's width and layout parameters programmatically.
        val width = (context.resources.displayMetrics.widthPixels * 0.8).toInt() // Set width to 80% of screen width.
        val window = dialogBuilder.window // Get the dialog's window reference.

        // Set the layout parameters to position the dialog in the center.
        window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

        // Optionally, you can further customize the dialog's appearance (padding, margins, etc.).
        val layoutParams = window?.attributes
        layoutParams?.gravity = android.view.Gravity.CENTER // Center the dialog on the screen.
        window?.attributes = layoutParams // Apply the modified layout parameters to the window.
    }
}