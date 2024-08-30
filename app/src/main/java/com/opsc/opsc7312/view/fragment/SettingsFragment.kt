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
import com.opsc.opsc7312.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        buttonEvents()

        return binding.root
    }

    private fun buttonEvents(){
        binding.accountSection.setOnClickListener {
            changeCurrentFragment(ProfileFragment())
        }

        binding.languageSection.setOnClickListener {
            changeCurrentFragment(LanguageFragment())
        }

        binding.securitySection.setOnClickListener {
            changeCurrentFragment(SecurityFragment())
        }

        binding.appearanceSection.setOnClickListener {
            changeCurrentFragment(ThemeFragment())
        }

        binding.notificationsSection.setOnClickListener {
            changeCurrentFragment(NotificationsFragment())
        }
    }

    private fun changeCurrentFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }
}
