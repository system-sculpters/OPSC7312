package com.opsc.opsc7312.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.opsc.opsc7312.R
import android.content.res.Configuration
import java.util.Locale

class LanguageFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var languageSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragement_languages, container, false)

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)

        // Initialize Spinner
        languageSpinner = view.findViewById(R.id.languageSpinner)

        // Set up the Spinner with English, Afrikaans, and Zulu
        val languages = arrayOf("English", "Afrikaans", "Zulu")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        // Load saved language preference and set Spinner selection
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "English")
        languageSpinner.setSelection(languages.indexOf(savedLanguage))

        // Handle language selection
        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLanguage = parent.getItemAtPosition(position).toString()
                saveLanguagePreference(selectedLanguage)
                setLocale(selectedLanguage)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        return view
    }

    private fun saveLanguagePreference(language: String) {
        with(sharedPreferences.edit()) {
            putString("selectedLanguage", language)
            apply()
        }
    }

    private fun setLocale(language: String) {
        val localeCode = when (language) {
            "Afrikaans" -> "af"
            "Zulu" -> "zu"
            else -> "en" // Default to English
        }

        val locale = Locale(localeCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        requireContext().createConfigurationContext(config)

        // Refresh the UI to reflect the new language
        refreshUI()
    }

    private fun refreshUI() {
        // You need to manually update the text for each UI component here
        // Example:
        // languageSpinner.setSelection(languages.indexOf(getString(R.string.language)))
        // Additional UI updates as needed

        // Alternatively, navigate back to the previous fragment or refresh the fragment itself
        parentFragmentManager.beginTransaction().detach(this).attach(this).commit()
    }
}
