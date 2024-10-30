package com.opsc.opsc7312.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R

//A Fragment that allows users to manage notification preferences within the application.
class NotificationsFragment : Fragment() {

    // Switches for various notification settings
    private lateinit var switchNotifications: SwitchCompat
    private lateinit var switchNotifyPurchase: SwitchCompat
    private lateinit var switchNotifyGoalProgress: SwitchCompat
    private lateinit var switchAlertGoalReached: SwitchCompat
    private lateinit var switchNotifyProfileUpdated: SwitchCompat
    private lateinit var switchAlertNewLogins: SwitchCompat

    // SharedPreferences for storing notification preferences
    private lateinit var sharedPreferences: SharedPreferences

    //Called to inflate the layout for this fragment.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        // Initialize switches
        // This radio button setOnClickListener was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/radiobutton-in-kotlin/
        // bibeksah36
        // https://www.geeksforgeeks.org/user/bibeksah36/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        switchNotifications = view.findViewById(R.id.switch_notifications)
        switchNotifyPurchase = view.findViewById(R.id.switch_notify_purchase)
        switchNotifyGoalProgress = view.findViewById(R.id.switch_notify_goal_progress)
        switchAlertGoalReached = view.findViewById(R.id.switch_alert_goal_reached)
        switchNotifyProfileUpdated = view.findViewById(R.id.switch_notify_profile_updated)
        switchAlertNewLogins = view.findViewById(R.id.switch_alert_new_logins)

        // Apply custom colors to the switches
        applySwitchStyles()

        // Load saved preferences from SharedPreferences to set switch states
        loadPreferences()

        // Set up listeners for each switch to save preferences when changed
        setupListeners()

        return view
    }

    //Called after the fragment's view has been created. This method sets the toolbar title
    //to "Notifications" in the MainActivity for user context.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access the MainActivity and set the toolbar title
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.notification))
    }

    //Applies custom styles to the switches by creating ColorStateLists for thumb and track tint.
    private fun applySwitchStyles() {
        // Create ColorStateList for thumb tint
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
        // Harneet Kaur
        // https://stackoverflow.com/users/1444525/harneet-kaur
        // Ziem

        val thumbColorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),  // Checked state
                intArrayOf(-android.R.attr.state_checked) // Unchecked state
            ),
            intArrayOf(
                ContextCompat.getColor(requireContext(), R.color.primary), // Color when checked
                textColor() // Color when unchecked
            )
        )

        // Create ColorStateList for track tint
        val trackColorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),  // Checked state
                intArrayOf(-android.R.attr.state_checked) // Unchecked state
            ),
            intArrayOf(
                ContextCompat.getColor(requireContext(), R.color.primary), // Color when checked
                textColor() // Color when unchecked
            )
        )

        // Apply thumb and track tint to each switch
        // This radio button setOnClickListener was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/radiobutton-in-kotlin/
        // bibeksah36
        // https://www.geeksforgeeks.org/user/bibeksah36/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        switchNotifications.apply {
            thumbTintList = thumbColorStateList
            trackTintList = trackColorStateList
        }
        switchNotifyPurchase.apply {
            thumbTintList = thumbColorStateList
            trackTintList = trackColorStateList
        }
        switchNotifyGoalProgress.apply {
            thumbTintList = thumbColorStateList
            trackTintList = trackColorStateList
        }
        switchAlertGoalReached.apply {
            thumbTintList = thumbColorStateList
            trackTintList = trackColorStateList
        }
        switchNotifyProfileUpdated.apply {
            thumbTintList = thumbColorStateList
            trackTintList = trackColorStateList
        }
        switchAlertNewLogins.apply {
            thumbTintList = thumbColorStateList
            trackTintList = trackColorStateList
        }
    }

    //Sets up listeners for each switch. When the state of a switch changes,
    //the corresponding preference is saved to SharedPreferences.
    private fun setupListeners() {
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            // Save preference for notification enabled state
            // This method was adapted from stackoverflow
            // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
            // Harneet Kaur
            // https://stackoverflow.com/users/1444525/harneet-kaur
            // Ziem
            sharedPreferences.edit().putBoolean("notifications_enabled", isChecked).apply()
        }

        switchNotifyPurchase.setOnCheckedChangeListener { _, isChecked ->
            // Save preference for purchase notifications
            // This method was adapted from stackoverflow
            // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
            // Harneet Kaur
            // https://stackoverflow.com/users/1444525/harneet-kaur
            // Ziem
            sharedPreferences.edit().putBoolean("notify_purchase", isChecked).apply()
        }

        switchNotifyGoalProgress.setOnCheckedChangeListener { _, isChecked ->
            // Save preference for goal progress notifications
            // This method was adapted from stackoverflow
            // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
            // Harneet Kaur
            // https://stackoverflow.com/users/1444525/harneet-kaur
            // Ziem
            sharedPreferences.edit().putBoolean("notify_goal_progress", isChecked).apply()
        }

        switchAlertGoalReached.setOnCheckedChangeListener { _, isChecked ->
            // Save preference for goal reached alerts
            // This method was adapted from stackoverflow
            // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
            // Harneet Kaur
            // https://stackoverflow.com/users/1444525/harneet-kaur
            // Ziem
            sharedPreferences.edit().putBoolean("alert_goal_reached", isChecked).apply()
        }

        switchNotifyProfileUpdated.setOnCheckedChangeListener { _, isChecked ->
            // Save preference for profile update notifications
            // This method was adapted from stackoverflow
            // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
            // Harneet Kaur
            // https://stackoverflow.com/users/1444525/harneet-kaur
            // Ziem
            sharedPreferences.edit().putBoolean("notify_profile_updated", isChecked).apply()
        }

        switchAlertNewLogins.setOnCheckedChangeListener { _, isChecked ->
            // Save preference for new login alerts
            // This method was adapted from stackoverflow
            // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
            // Harneet Kaur
            // https://stackoverflow.com/users/1444525/harneet-kaur
            // Ziem
            sharedPreferences.edit().putBoolean("alert_new_logins", isChecked).apply()
        }
    }

    //Loads preferences from SharedPreferences and sets the state of each switch accordingly.
    private fun loadPreferences() {
        // Load preferences from SharedPreferences and set the switch states
        // This radio button setOnClickListener was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/radiobutton-in-kotlin/
        // bibeksah36
        // https://www.geeksforgeeks.org/user/bibeksah36/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        switchNotifications.isChecked = sharedPreferences.getBoolean("notifications_enabled", true)
        switchNotifyPurchase.isChecked = sharedPreferences.getBoolean("notify_purchase", true)
        switchNotifyGoalProgress.isChecked = sharedPreferences.getBoolean("notify_goal_progress", true)
        switchAlertGoalReached.isChecked = sharedPreferences.getBoolean("alert_goal_reached", true)
        switchNotifyProfileUpdated.isChecked = sharedPreferences.getBoolean("notify_profile_updated", true)
        switchAlertNewLogins.isChecked = sharedPreferences.getBoolean("alert_new_logins", true)
    }

    //Returns the text color defined in the app theme for use in the switch styles.
    private fun textColor(): Int {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.themeBgBorder, typedValue, true) // Resolve the attribute
        val color = typedValue.data // Get the resolved color
        return color // Return the color
    }
}

