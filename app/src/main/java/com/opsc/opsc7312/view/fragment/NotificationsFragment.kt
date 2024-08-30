package com.opsc.opsc7312.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.opsc.opsc7312.R

class NotificationsFragment : Fragment() {

    private lateinit var switchNotifications: Switch
    private lateinit var switchNotifyPurchase: Switch
    private lateinit var switchNotifyGoalProgress: Switch
    private lateinit var switchAlertGoalReached: Switch
    private lateinit var switchNotifyProfileUpdated: Switch
    private lateinit var switchAlertNewLogins: Switch

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        // Initialize switches
        switchNotifications = view.findViewById(R.id.switch_notifications)
        switchNotifyPurchase = view.findViewById(R.id.switch_notify_purchase)
        switchNotifyGoalProgress = view.findViewById(R.id.switch_notify_goal_progress)
        switchAlertGoalReached = view.findViewById(R.id.switch_alert_goal_reached)
        switchNotifyProfileUpdated = view.findViewById(R.id.switch_notify_profile_updated)
        switchAlertNewLogins = view.findViewById(R.id.switch_alert_new_logins)

        // Apply custom colors
        applySwitchStyles()

        // Load saved preferences
        loadPreferences()

        // Set up listeners
        setupListeners()

        return view
    }

    private fun applySwitchStyles() {
        // Create ColorStateList for thumb tint
        val thumbColorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),  // On
                intArrayOf(-android.R.attr.state_checked) // Off
            ),
            intArrayOf(
                ContextCompat.getColor(requireContext(), R.color.primary),
                ContextCompat.getColor(requireContext(), R.color.dark_grey)
            )
        )

        // Create ColorStateList for track tint
        val trackColorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),  // On
                intArrayOf(-android.R.attr.state_checked) // Off
            ),
            intArrayOf(
                ContextCompat.getColor(requireContext(), R.color.primary),
                ContextCompat.getColor(requireContext(), R.color.dark_grey)
            )
        )

        // Apply thumb and track tint to each switch
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

    private fun setupListeners() {
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            // Save preference
            sharedPreferences.edit().putBoolean("notifications_enabled", isChecked).apply()
        }

        switchNotifyPurchase.setOnCheckedChangeListener { _, isChecked ->
            // Save preference
            sharedPreferences.edit().putBoolean("notify_purchase", isChecked).apply()
        }

        switchNotifyGoalProgress.setOnCheckedChangeListener { _, isChecked ->
            // Save preference
            sharedPreferences.edit().putBoolean("notify_goal_progress", isChecked).apply()
        }

        switchAlertGoalReached.setOnCheckedChangeListener { _, isChecked ->
            // Save preference
            sharedPreferences.edit().putBoolean("alert_goal_reached", isChecked).apply()
        }

        switchNotifyProfileUpdated.setOnCheckedChangeListener { _, isChecked ->
            // Save preference
            sharedPreferences.edit().putBoolean("notify_profile_updated", isChecked).apply()
        }

        switchAlertNewLogins.setOnCheckedChangeListener { _, isChecked ->
            // Save preference
            sharedPreferences.edit().putBoolean("alert_new_logins", isChecked).apply()
        }
    }

    private fun loadPreferences() {
        // Load preferences
        switchNotifications.isChecked = sharedPreferences.getBoolean("notifications_enabled", false)
        switchNotifyPurchase.isChecked = sharedPreferences.getBoolean("notify_purchase", false)
        switchNotifyGoalProgress.isChecked = sharedPreferences.getBoolean("notify_goal_progress", false)
        switchAlertGoalReached.isChecked = sharedPreferences.getBoolean("alert_goal_reached", false)
        switchNotifyProfileUpdated.isChecked = sharedPreferences.getBoolean("notify_profile_updated", false)
        switchAlertNewLogins.isChecked = sharedPreferences.getBoolean("alert_new_logins", false)
    }
}
