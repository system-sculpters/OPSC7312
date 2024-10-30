package com.opsc.opsc7312.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentProfileBinding
import com.opsc.opsc7312.databinding.FragmentSecurityBinding


class SecurityFragment : Fragment() {
    // Binding for the fragment's view to access UI elements
    private var _binding: FragmentSecurityBinding? = null
    private val binding get() = _binding!! // Property to get non-nullable binding reference

    private lateinit var sharedPreferences: SharedPreferences // To store user preferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using View Binding
        _binding = FragmentSecurityBinding.inflate(inflater, container, false)

        // Initialize SharedPreferences to store biometric settings
        sharedPreferences =
            requireActivity().getSharedPreferences("SecurityPreferences", Context.MODE_PRIVATE)

        // Get reference to the biometrics switch from the layout
        val biometricsSwitch = binding.switchBiometrics

        // Set the initial state of the switch based on saved preferences
        biometricsSwitch.isChecked = getBiometricPreference()
        // Update the switch colors based on its current state
        updateSwitchColors(biometricsSwitch.isChecked, biometricsSwitch)

        // Set up listener for the switch to handle changes
        biometricsSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save the new preference when the switch is toggled
            saveBiometricPreference(isChecked)
            // Update the colors of the switch based on its state
            updateSwitchColors(isChecked, biometricsSwitch)
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.security))
    }

    // Retrieve the saved biometric preference, default to false if not set
    private fun getBiometricPreference(): Boolean {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
        // Harneet Kaur
        // https://stackoverflow.com/users/1444525/harneet-kaur
        // Ziem
        // https://stackoverflow.com/posts/11027631/revisions
        return sharedPreferences.getBoolean("biometric_enabled", true)
    }

    // Save the biometric preference to SharedPreferences
    private fun saveBiometricPreference(isChecked: Boolean) {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
        // Harneet Kaur
        // https://stackoverflow.com/users/1444525/harneet-kaur
        // Ziem
        // https://stackoverflow.com/posts/11027631/revisions
        with(sharedPreferences.edit()) {
            putBoolean("biometric_enabled", isChecked) // Save the preference
            apply() // Apply changes asynchronously
        }
    }

    // Update the colors of the switch based on its checked state
    private fun updateSwitchColors(isChecked: Boolean, biometricsSwitch: Switch) {
        // This radio button setOnClickListener was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/radiobutton-in-kotlin/
        // bibeksah36
        // https://www.geeksforgeeks.org/user/bibeksah36/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        if (isChecked) {
            // Set colors for the switch when it is checked
            biometricsSwitch.thumbTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.primary)
            biometricsSwitch.trackTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.primary)
        } else {
            // Set colors for the switch when it is unchecked
            biometricsSwitch.thumbTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.light_gray)
            biometricsSwitch.trackTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.darkBackgroundColor)
        }
    }

    // Clean up binding object when the fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
