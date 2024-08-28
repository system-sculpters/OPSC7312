package com.example.yourapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.opsc.opsc7312.R
import com.yourpackage.LanguageFragment

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Find the language section LinearLayout
        val languageSection = view.findViewById<LinearLayout>(R.id.language_section)

        // Set click listener to navigate to LanguageFragment
        languageSection.setOnClickListener {
            // Check if this fragment is attached to an activity
            if (isAdded) {
                // Replace the current fragment with LanguageFragment
                parentFragmentManager.beginTransaction()
                    .replace(R.id.language_section, LanguageFragment())  // Using LinearLayout as the container
                    .addToBackStack(null)  // This ensures that the user can navigate back
                    .commit()
            }
        }

        return view
    }
}
