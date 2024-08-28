package com.example.yourapp.fragments

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

class LanguageFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var languageSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.f, container, false)

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

        // Initialize Spinner
        languageSpinner = view.findViewById(R.id.spinner_languages)

        setupSpinner()

        return view
    }

    private fun setupSpinner() {
        // Get the saved language preference
        val savedLanguage = sharedPreferences.getString("selected_language", "English")

        // Set the spinner adapter
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.language_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            languageSpinner.adapter = adapter
        }

        // Set the saved language as selected
        val languageIndex = resources.getStringArray(R.array.language_array).indexOf(savedLanguage)
        languageSpinner.setSelection(languageIndex)

        // Set listener for spinner selection
        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLanguage = parent?.getItemAtPosition(position).toString()
                saveLanguagePreference(selectedLanguage)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun saveLanguagePreference(language: String) {
        // Save the selected language to SharedPreferences
        with(sharedPreferences.edit()) {
            putString("selected_language", language)
            apply()
        }
    }
}
