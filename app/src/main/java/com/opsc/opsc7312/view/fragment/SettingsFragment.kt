package com.opsc.opsc7312.view.fragment

import ProfileFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.app.ui.notifications.NotificationsFragment
import com.example.app.LanguageFragment
import com.example.app.ui.security.SecurityFragment
import com.example.yourapp.ThemeFragment
import com.opsc.opsc7312.R

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate to LanguageFragment when the language section is clicked
        val languageSection = view.findViewById<LinearLayout>(R.id.language_section)
        languageSection.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, LanguageFragment())
                .addToBackStack(null)
                .commit()
        }

        // Navigate to SecurityFragment when the security section is clicked
        val securitySection = view.findViewById<LinearLayout>(R.id.security_section)
        securitySection.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, SecurityFragment())
                .addToBackStack(null)
                .commit()
        }

        // Navigate to NotificationsFragment when the notifications section is clicked
        val notificationsSection = view.findViewById<LinearLayout>(R.id.notifications_section)
        notificationsSection.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, NotificationsFragment())
                .addToBackStack(null)
                .commit()
        }

        // Navigate to ProfileFragment when the Username section is clicked
        val usernameSection = view.findViewById<LinearLayout>(R.id.account_section)
        usernameSection.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        // Navigate to ThemeFragment when the appearance/theme section is clicked
        val appearanceSection = view.findViewById<LinearLayout>(R.id.appearance_section)
        appearanceSection.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ThemeFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}
