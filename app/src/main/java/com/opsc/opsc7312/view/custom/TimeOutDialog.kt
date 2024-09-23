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
            if(message.contains("fail") || message.contains("unsuccessful")){
                checkmarkImageView.setImageResource(R.drawable.ic_close)
                val color = ContextCompat.getColor(context, R.color.red) // Fetch color from resources
                checkmarkImageView.setColorFilter(color)
            } else {
                checkmarkImageView.setImageResource(R.drawable.baseline_check_green)
                val color = ContextCompat.getColor(context, R.color.green) // Fetch color from resources
                checkmarkImageView.setColorFilter(color)
            }
            val fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
            checkmarkImageView.startAnimation(fadeIn)
            checkmarkImageView?.visibility = ImageView.VISIBLE
        } else {
            checkmarkImageView?.visibility = ImageView.GONE
        }
    }

    fun showAlertDialog(context: Context, errorMessage: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.input_error_dialog, null)

        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialogMessage)
        val dialogBtn = dialogView.findViewById<TextView>(R.id.dismissButton)

        dialogMessage.text = errorMessage

        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        dialogBtn.setOnClickListener {
            dialogBuilder.dismiss()
        }

        dialogBuilder.show()

        // Adjust the dialog width and apply margins programmatically
        val width = (context.resources.displayMetrics.widthPixels * 0.8).toInt() // 80% of screen width
        val window = dialogBuilder.window// Set background if needed

        window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)

        // Optionally, set padding or margin around the dialog

        val layoutParams = window?.attributes
        layoutParams?.gravity = android.view.Gravity.CENTER // Example margin (10% of the screen)
        window?.attributes = layoutParams
    }
}