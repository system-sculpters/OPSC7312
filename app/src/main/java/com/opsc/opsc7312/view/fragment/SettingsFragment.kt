package com.opsc.opsc7312.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentSettingsBinding
import com.opsc.opsc7312.model.data.offline.preferences.UserManager

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var userManager: UserManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        userManager = UserManager.getInstance(requireContext())

        binding.username.text = userManager.getUser().username
        binding.email.text = userManager.getUser().email

        buttonEvents()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access the MainActivity and set the toolbar title
        (activity as? MainActivity)?.setToolbarTitle("Settings")
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
