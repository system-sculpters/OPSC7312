package com.yourpackage

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.opsc.opsc7312.R

class LanguageFragment : Fragment() {

    private lateinit var languageSpinner: Spinner
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragement_languages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        languageSpinner = view.findViewById(R.id.language_spinner)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        // Set up the spinner
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.languages_array,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        languageSpinner.adapter = adapter

        // Set the default selection
        val currentLanguage = sharedPreferences.getString("language", "English")
        val position = adapter.getPosition(currentLanguage)
        languageSpinner.setSelection(position)

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLanguage = adapter.getItem(position).toString()
                sharedPreferences.edit().putString("language", selectedLanguage).apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case when no item is selected (optional)
            }
        }
    }
}
