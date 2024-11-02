package com.opsc.opsc7312

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Object that holds constant values and utility functions for the application.
// This object provides essential configuration settings, enumerations for transaction types,
// and utility functions for date and amount formatting.

object AppConstants {

    // The base URL for the API endpoints used throughout the application.
    // This URL is used as the prefix for all network requests.
    const val BASE_URL = "https://pennywise-1rw5.onrender.com/api/"

    //const val BASE_URL = "https://localhost:3001/api/"

    const val UNCATEGORIZED = "wEijTYKaY8738zHM4oJb"

    // Enumeration defining the types of transactions available in the application.
    // The types include INCOME and EXPENSE, allowing for categorization of financial transactions.
    enum class TRANSACTIONTYPE {
        INCOME, EXPENSE
    }

    // Enumeration defining the types of contributions available for financial planning.
    // This includes options for WEEKLY, BIWEEKLY, and MONTHLY contributions, helping users
    // plan their savings or investments effectively.
    enum class CONTRIBUTIONTYPE {
        WEEKLY, BIWEEKLY, MONTHLY
    }

    // Enumeration for the various sorting options available for displaying lists of data.
    // The sorting options include ascending and descending order based on name or date,
    // as well as sorting by the highest or lowest amount.
    enum class SORT_TYPE {
        NAME_ASCENDING, NAME_DESCENDING, DATE_ASCENDING,
        DATE_DESCENDING, HIGHEST_AMOUNT, LOWEST_AMOUNT
    }

    // Enumeration for filtering options available for displaying transactions.
    // The filter options include ALL, INCOME, and EXPENSE, allowing users to view
    // specific subsets of their transaction history.
    enum class Filter_TYPE {
        ALL, INCOME, EXPENSE,
    }

    // A dictionary mapping color names to their corresponding color resource IDs.
    // This is used throughout the application to facilitate color selection for various UI elements.
    val COLOR_DICTIONARY = mapOf(
        "Red" to R.color.red,
        "Blue" to R.color.blue,
        "Green" to R.color.green,
        "Yellow" to R.color.yellow
    )

    // A dictionary mapping color names to their corresponding checkmark image resource IDs.
    // This can be used to visually indicate selection of specific colors in the UI.
    val COLOR_IMAGE_DICTIONARY = mapOf(
        "Red" to R.drawable.baseline_check_red,
        "Blue" to R.drawable.baseline_check_blue,
        "Green" to R.drawable.baseline_check_green,
        "Yellow" to R.drawable.baseline_check_yellow
    )

    // A list of color names available for selection in the application.
    // This list can be used to populate color picker UI elements.
    val COLOR_LIST = arrayListOf(
        "Red", "Blue", "Green", "Yellow"
    )

    // A dictionary mapping categories of expenses to their corresponding icon resource IDs.
    // This allows the application to display relevant icons next to transaction categories for better UX.
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
        "gift" to R.drawable.baseline_card_giftcard_24,
        "travel" to R.drawable.baseline_airplanemode_active_24,
        "pets" to R.drawable.baseline_pets_24,
        "taxes" to R.drawable.baseline_calculate_24,
        "transportation" to R.drawable.baseline_directions_bus_24,
        "petrol/gas" to R.drawable.baseline_local_gas_station_24,
        "subscription" to R.drawable.baseline_loop_24
    )

    // Converts a timestamp (in milliseconds) to a formatted date string (dd/MM/yyyy).
    // This function is useful for displaying dates in a user-friendly format.
    fun convertLongToString(timestamp: Long): String {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/57402045/how-to-format-in-kotlin-date-in-string-or-timestamp-to-my-preferred-format
        // https://stackoverflow.com/users/11555903/ben-shmuel
        // Ben Shmuel
        val sdf: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        val date: Date = Date(timestamp)
        val formattedDate: String = sdf.format(date)
        return formattedDate
    }

    // Checks whether the token has expired based on the provided expiration time.
    // Returns true if the current system time exceeds the expiration time, indicating
    // that the token is no longer valid for authentication.
    fun isTokenExpired(expirationTime: Long): Boolean {
        return System.currentTimeMillis() > expirationTime
    }

    // Calculates the expiration time for a token, which is set to two days from the current time.
    // This function can be used to set a validity period for authentication tokens.
    fun tokenExpirationTime(): Long {
        return System.currentTimeMillis() + (2 * 24 * 60 * 60 * 1000)
    }

    // Formats a given amount as a string with two decimal places.
    // This function is useful for displaying monetary values in a consistent format throughout the app.
    fun formatAmount(amount: Double): String {
        return String.format(Locale.US, "%.2f", amount)
    }

    // Converts a timestamp (in milliseconds) to a formatted date string (dd/MM/yyyy).
    // This function is similar to convertLongToString and can be used for date formatting tasks.
    fun longToDate(timestamp: Long): String {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/57402045/how-to-format-in-kotlin-date-in-string-or-timestamp-to-my-preferred-format
        // https://stackoverflow.com/users/11555903/ben-shmuel
        // Ben Shmuel
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val formattedDate = sdf.format(date)
        return formattedDate
    }

    // Converts a date string in "dd/MM/yyyy" format to a Long timestamp
    fun convertStringToLong(dateString: String): Long {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/57402045/how-to-format-in-kotlin-date-in-string-or-timestamp-to-my-preferred-format
        // https://stackoverflow.com/users/11555903/ben-shmuel
        // Ben Shmuel

        // Create a date format object for parsing the date string
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Parse the date string into a Date object
        val date: Date = dateFormat.parse(dateString) ?: throw IllegalArgumentException("Invalid date format")

        // Return the timestamp of the date
        return date.time
    }
}
