package com.opsc.opsc7312.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Switch
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R


class SecurityFragment : Fragment(R.layout.fragment_security) {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setToolbarTitle("Security")

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("SecurityPreferences", Context.MODE_PRIVATE)

        // Get reference to the switch
        val biometricsSwitch = view.findViewById<Switch>(R.id.switch_biometrics)

        // Set initial state or retrieve from saved preferences
        biometricsSwitch.isChecked = getBiometricPreference()
        updateSwitchColors(biometricsSwitch.isChecked, biometricsSwitch)

        // Set up listener for the switch
        biometricsSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save preference when the switch is toggled
            saveBiometricPreference(isChecked)
            updateSwitchColors(isChecked, biometricsSwitch)
        }
    }



    private fun getBiometricPreference(): Boolean {
        // Retrieve the saved biometric preference, default to false if not set
        return sharedPreferences.getBoolean("biometric_enabled", false)
    }

    private fun saveBiometricPreference(isChecked: Boolean) {
        // Save the biometric preference to SharedPreferences
        with(sharedPreferences.edit()) {
            putBoolean("biometric_enabled", isChecked)
            apply()
        }
    }

    private fun updateSwitchColors(isChecked: Boolean, biometricsSwitch: Switch) {
        if (isChecked) {
            biometricsSwitch.thumbTintList = ContextCompat.getColorStateList(requireContext(), R.color.primary)
            biometricsSwitch.trackTintList = ContextCompat.getColorStateList(requireContext(), R.color.primary)
        } else {
            biometricsSwitch.thumbTintList = ContextCompat.getColorStateList(requireContext(), R.color.light_gray)
            biometricsSwitch.trackTintList = ContextCompat.getColorStateList(requireContext(), R.color.darkBackgroundColor)
        }
    }
}
