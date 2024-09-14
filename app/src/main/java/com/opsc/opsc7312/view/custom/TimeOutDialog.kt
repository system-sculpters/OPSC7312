package com.opsc.opsc7312.view.custom

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.opsc.opsc7312.R

class TimeOutDialog {
    fun showTimeoutDialog(context: Context, onRetry: () -> Unit) {
        // Build the alert dialog
        val dialogBuilder = AlertDialog.Builder(context)

        // Set the message and buttons
        dialogBuilder.setMessage("Connection timed out. Please check your connection and try again.")
            .setCancelable(false)
            .setPositiveButton("Retry") { dialog, id ->
                // Retry action
                onRetry()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, id ->
                // Dismiss the dialog and do nothing
                dialog.dismiss()
            }

        // Create the alert dialog and show it
        val alert = dialogBuilder.create()
        alert.setTitle("Connection Timeout")
        alert.show()
    }


    fun showProgressDialog(context: Context): AlertDialog {
        // Inflate the custom dialog layout with the progress bar
        val dialogView = LayoutInflater.from(context).inflate(R.layout.timeout_popup_dialog, null)

        // Create the AlertDialog with the custom layout
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)

        // Create and show the dialog
        val dialog = dialogBuilder.create()
        dialog.show()

        return dialog
    }

    fun updateProgressDialog(context: Context,dialog: AlertDialog, message: String, hideProgressBar: Boolean) {
        // Access the progress bar, checkmark image, and text view
        val progressBar = dialog.findViewById<ProgressBar>(R.id.progressBar)
        val checkmarkImageView = dialog.findViewById<ImageView>(R.id.checkmarkImageView)
        val statusTextView = dialog.findViewById<TextView>(R.id.statusTextView)

        // Update the status message
        statusTextView?.text = message

        // Show or hide the progress bar
        if (hideProgressBar) {
            progressBar?.visibility = ProgressBar.GONE
        } else {
            progressBar?.visibility = ProgressBar.VISIBLE
        }

        // Show or hide the checkmark
        if (hideProgressBar) {
            val fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
            checkmarkImageView.startAnimation(fadeIn)
            checkmarkImageView?.visibility = ImageView.VISIBLE
        } else {
            checkmarkImageView?.visibility = ImageView.GONE
        }
    }

}