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

    private var _binding: FragmentThemeBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThemeBinding.inflate(inflater, container, false)

        //toolbarTitle.text = "Theme/Appearance"

        // Initialize SharedPreferences
        sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())

        // Load saved theme preference and apply the theme
        loadSavedTheme()

        // Set up listeners for theme selection
        setupThemeSelectionListeners()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access the MainActivity and set the toolbar title
        (activity as? MainActivity)?.setToolbarTitle("Theme")
    }

    private fun setupThemeSelectionListeners() {
        binding.lightMode.setOnClickListener {
            selectTheme("Light")
        }

        binding.darkMode.setOnClickListener {
            selectTheme("Dark")
        }

        binding.systemMode.setOnClickListener {
            selectTheme("Automatic")
        }
    }

    private fun selectTheme(theme: String) {
        saveThemePreference(theme)
        applyTheme(theme)
    }

    private fun applyTheme(theme: String) {
        when (theme) {
            "Light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "Automatic" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        // Restart activity to apply the new theme
        activity?.recreate()
    }

    private fun loadSavedTheme() {
        // Fetch the saved theme preference, default to "Light" if not found
        val savedTheme = sharedPreferences.getString("theme_preference", "Light") ?: "Light"

        // Update the UI to reflect the saved theme
        updateCheckIcons(savedTheme)
    }

    private fun updateCheckIcons(theme: String) {
        binding.isCheckedLight.visibility = if (theme == "Light") View.VISIBLE else View.INVISIBLE
        binding.isCheckedDark.visibility = if (theme == "Dark") View.VISIBLE else View.INVISIBLE
        binding.isCheckedSystem.visibility = if (theme == "Automatic") View.VISIBLE else View.INVISIBLE
    }

    private fun saveThemePreference(theme: String) {
        with(sharedPreferences.edit()) {
            putString("theme_preference", theme)
            apply() // Apply changes asynchronously
        }
    }
}
