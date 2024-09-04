package com.opsc.opsc7312

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date

object AppConstants {

    const val BASE_URL = "https://pennywise-1rw5.onrender.com/api/"

    private val toastList = mutableListOf<View>()
    private val toastHeight = 150 // Adjust based on your toast height
    private var currentY = 150 // Keeps track of the current y position for new toasts


    enum class TRANSACTIONTYPE{
        INCOME, EXPENSE
    }

    enum class CONTRIBUTIONTYPE{
        WEEKLY, BIWEEKLY, MONTHLY
    }

    val COLOR_DICTIONARY = mapOf(
        "Red" to R.color.red,
        "Blue" to R.color.blue,
        "Green" to R.color.green,
        "Yellow" to R.color.yellow
    )

    // Map of colors with their corresponding checkmark image resource IDs
    val COLOR_IMAGE_DICTIONARY = mapOf(
        "Red" to R.drawable.baseline_check_red,
        "Blue" to R.drawable.baseline_check_blue,
        "Green" to R.drawable.baseline_check_green,
        "Yellow" to R.drawable.baseline_check_yellow
    )

    // List of color names
    val COLOR_LIST = arrayListOf(
        "Red" ,"Blue","Green","Yellow"
    )

    val ICONS = mapOf(
        "groceries" to R.drawable.baseline_shopping_cart_24,
        "dining out" to R.drawable.baseline_local_dining_24,
        "housing" to R.drawable.baseline_house_24,
        "utilities" to R.drawable.baseline_lightbulb_24,
        "insurance" to R.drawable.baseline_shield_24,
        "entertainment" to R.drawable.baseline_movie_24,
        "healthcare" to R.drawable.baseline_medical_services_24,
        "education" to R.drawable.baseline_school_24,
        "personal care" to R.drawable.baseline_location_on_24,
//        "email" to R.drawable.baseline_email_24,
//        "book" to R.drawable.baseline_menu_book_24,
//        "settings" to R.drawable.baseline_settings_24,
        "gift" to R.drawable.baseline_card_giftcard_24,
        "travel" to R.drawable.baseline_airplanemode_active_24,
        "pets" to R.drawable.baseline_pets_24,
        "taxes" to R.drawable.baseline_calculate_24,
        "transportation" to R.drawable.baseline_directions_bus_24,
        "petrol/gas" to R.drawable.baseline_local_gas_station_24,
        "subscription" to R.drawable.baseline_loop_24
    )

    fun convertLongToString(timestamp: Long): String {
        val sdf: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        val date: Date = Date(timestamp)
        val formattedDate: String = sdf.format(date)
        return formattedDate
    }



    // Define at the class level

    fun showFloatingToast(context: Context, message: String) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val inflater = LayoutInflater.from(context)
        val layout: View = inflater.inflate(R.layout.custom_toast, null)

        val text: TextView = layout.findViewById(R.id.toast_text)
        text.text = message

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )

        // Set the position of the new toast
        params.gravity = Gravity.TOP or Gravity.END
        params.x = 0
        params.y = currentY

        windowManager.addView(layout, params)
        toastList.add(layout)

        // Update currentY for next toast
        currentY += toastHeight

        // Remove the toast after a delay
        layout.postDelayed({
            windowManager.removeView(layout)
            toastList.remove(layout)

            // Adjust positions of remaining toasts
            currentY -= toastHeight
            for (i in toastList.indices) {
                val updatedParams = toastList[i].layoutParams as WindowManager.LayoutParams
                updatedParams.y = i * toastHeight // Adjust the offset as needed
                windowManager.updateViewLayout(toastList[i], updatedParams)
            }
        }, 5000) // Duration of the toast
    }

    fun isTokenExpired(expirationTime: Long): Boolean {
        return System.currentTimeMillis() > expirationTime
    }

}