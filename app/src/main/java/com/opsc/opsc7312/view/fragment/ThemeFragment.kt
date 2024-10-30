package com.opsc.opsc7312.view.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentThemeBinding

class ThemeFragment : Fragment() {

    // Binding for the fragment's view to access UI elements
    private var _binding: FragmentThemeBinding? = null
    private val binding get() = _binding!! // Non-nullable reference for easy access

    // SharedPreferences instance to store and retrieve theme preferences
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using View Binding
        _binding = FragmentThemeBinding.inflate(inflater, container, false)

        // Initialize SharedPreferences to access saved user preferences
        sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())

        // Load the saved theme preference and apply the appropriate theme
        loadSavedTheme()

        // Set up listeners for theme selection options
        setupThemeSelectionListeners()

        // Return the root view of the binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the toolbar title for the activity to "Theme"
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.theme))
    }

    // Function to set up click listeners for theme selection options
    private fun setupThemeSelectionListeners() {
        // Listener for light mode selection
        binding.lightMode.setOnClickListener {
            selectTheme("Light")
        }

        // Listener for dark mode selection
        binding.darkMode.setOnClickListener {
            selectTheme("Dark")
        }

        // Listener for automatic mode selection
        binding.systemMode.setOnClickListener {
            selectTheme("Automatic")
        }
    }

    // Function to save the selected theme and apply it
    private fun selectTheme(theme: String) {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
        // Harneet Kaur
        // https://stackoverflow.com/users/1444525/harneet-kaur
        // Ziem
        // https://stackoverflow.com/posts/11027631/revisions
        saveThemePreference(theme) // Save the selected theme preference
        applyTheme(theme) // Apply the selected theme
    }

    // Function to apply the selected theme using AppCompatDelegate
    private fun applyTheme(theme: String) {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
        // Harneet Kaur
        // https://stackoverflow.com/users/1444525/harneet-kaur
        // Ziem
        // https://stackoverflow.com/posts/11027631/revisions
        when (theme) {
            "Light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // Set light mode
            "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) // Set dark mode
            "Automatic" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) // Set automatic mode
        }
        // Restart the activity to apply the new theme settings
        activity?.recreate()
    }

    // Function to load the saved theme preference and update the UI accordingly
    private fun loadSavedTheme() {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
        // Harneet Kaur
        // https://stackoverflow.com/users/1444525/harneet-kaur
        // Ziem
        // https://stackoverflow.com/posts/11027631/revisions

        // Fetch the saved theme preference, defaulting to "Light" if not set
        val savedTheme = sharedPreferences.getString("theme_preference", "Light") ?: "Light"

        // Update the UI to reflect the saved theme selection
        updateCheckIcons(savedTheme)
    }

    // Function to update the visibility of check icons based on the selected theme
    private fun updateCheckIcons(theme: String) {
        // Show or hide check icons based on the selected theme
        binding.isCheckedLight.visibility = if (theme == "Light") View.VISIBLE else View.INVISIBLE
        binding.isCheckedDark.visibility = if (theme == "Dark") View.VISIBLE else View.INVISIBLE
        binding.isCheckedSystem.visibility = if (theme == "Automatic") View.VISIBLE else View.INVISIBLE
    }

    // Function to save the theme preference in SharedPreferences
    private fun saveThemePreference(theme: String) {
        with(sharedPreferences.edit()) {
            putString("theme_preference", theme) // Save the selected theme
            apply() // Apply changes asynchronously
        }
    }

    // Clean up binding object when the fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
