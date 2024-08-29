package com.example.yourapp

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.opsc.opsc7312.R

class ThemeFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var checkLight: CheckBox
    private lateinit var checkDark: CheckBox
    private lateinit var checkAutomatic: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_theme, container, false)

        // Initialize CheckBox views
        checkLight = view.findViewById(R.id.checkLight)
        checkDark = view.findViewById(R.id.checkDark)
        checkAutomatic = view.findViewById(R.id.checkAutomatic)

        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        // Load saved theme preference
        loadSavedTheme()

        // Set up listeners for CheckBox changes
        setupCheckBoxListeners()

        return view
    }

    private fun setupCheckBoxListeners() {
        checkLight.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) setSingleCheck(checkLight)
        }
        checkDark.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) setSingleCheck(checkDark)
        }
        checkAutomatic.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) setSingleCheck(checkAutomatic)
        }
    }

    private fun setSingleCheck(selected: CheckBox) {
        // Uncheck other CheckBoxes
        if (checkLight != selected) checkLight.isChecked = false
        if (checkDark != selected) checkDark.isChecked = false
        if (checkAutomatic != selected) checkAutomatic.isChecked = false

        // Save the selected theme
        val theme = when (selected.id) {
            R.id.checkLight -> "Light"
            R.id.checkDark -> "Dark"
            R.id.checkAutomatic -> "Automatic"
            else -> "Light" // Default theme
        }
        saveThemePreference(theme)
    }

    private fun loadSavedTheme() {
        val savedTheme = sharedPreferences.getString("theme_preference", "Light")
        when (savedTheme) {
            "Light" -> checkLight.isChecked = true
            "Dark" -> checkDark.isChecked = true
            "Automatic" -> checkAutomatic.isChecked = true
        }
    }

    private fun saveThemePreference(theme: String) {
        with(sharedPreferences.edit()) {
            putString("theme_preference", theme)
            apply() // Apply changes asynchronously
        }
    }
}
