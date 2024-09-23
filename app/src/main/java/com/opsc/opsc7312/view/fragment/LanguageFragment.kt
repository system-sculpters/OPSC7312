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
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.databinding.FragmentCreateTransactionBinding
import com.opsc.opsc7312.databinding.FragmentLanguageBinding
import com.skydoves.powerspinner.PowerSpinnerView
import java.util.Locale

class LanguageFragment : Fragment() {
    private var _binding: FragmentLanguageBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLanguageBinding.inflate(inflater, container, false)

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)

        // Set up the Spinner with English, Afrikaans, and Zulu
        val languages = listOf("English", "Afrikaans", "Zulu")
        binding.languageSpinner.setItems(languages)


        // Load saved language preference and set Spinner selection
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "English")
        binding.languageSpinner.selectItemByIndex(languages.indexOf(savedLanguage))

        // Handle language selection
        // Assuming 'binding.languageSpinner' is your PowerSpinnerView instance
        binding.languageSpinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            // newItem is the selected language
            saveLanguagePreference(newItem)
            setLocale(newItem)
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access the MainActivity and set the toolbar title
        (activity as? MainActivity)?.setToolbarTitle("Language")
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
