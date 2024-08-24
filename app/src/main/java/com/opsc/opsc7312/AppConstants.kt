package com.opsc.opsc7312

object AppConstants {

    enum class TRANSACTION_TYPE{
        INCOME, EXPENSE
    }

    enum class CONTRIBUTION_TYPE{
        INCOME, EXPENSE
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
        "yellow" to R.drawable.baseline_check_yellow,
        "red" to R.drawable.baseline_check_red,
        "green" to R.drawable.baseline_check_green,
        "blue" to R.drawable.baseline_check_blue,

    )
}