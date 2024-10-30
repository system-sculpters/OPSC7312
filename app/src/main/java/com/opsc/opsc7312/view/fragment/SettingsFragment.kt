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

    // Binding for the fragment's view to access UI elements
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!! // Non-nullable binding reference for easy access

    // UserManager instance to manage user data
    private lateinit var userManager: UserManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using View Binding
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // Initialize UserManager to access user-related data
        userManager = UserManager.getInstance(requireContext())

        // Display the user's username and email in the respective TextViews
        binding.username.text = userManager.getUser().username
        binding.email.text = userManager.getUser().email

        // Set up button click events
        buttonEvents()

        // Return the root view of the binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the toolbar title for the activity to "Settings"
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.settings))
    }

    // Function to set up click listeners for various sections in the settings
    private fun buttonEvents() {
        // Listener for the account section, switches to ProfileFragment
        binding.accountSection.setOnClickListener {
            changeCurrentFragment(ProfileFragment())
        }

        // Listener for the language section, switches to LanguageFragment
        binding.languageSection.setOnClickListener {
            changeCurrentFragment(LanguageFragment())
        }

        // Listener for the security section, switches to SecurityFragment
        binding.securitySection.setOnClickListener {
            changeCurrentFragment(SecurityFragment())
        }

        // Listener for the appearance section, switches to ThemeFragment
        binding.appearanceSection.setOnClickListener {
            changeCurrentFragment(ThemeFragment())
        }

        // Listener for the notifications section, switches to NotificationsFragment
        binding.notificationsSection.setOnClickListener {
            changeCurrentFragment(NotificationsFragment())
        }
    }

    // Function to replace the current fragment with a new one and add it to the back stack
    private fun changeCurrentFragment(fragment: Fragment) {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/52318195/how-to-change-fragment-kotlin
        // Marcos Maliki
        // https://stackoverflow.com/users/8108169/marcos-maliki
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment) // Replace the content of the frame layout
            .addToBackStack(null) // Add the transaction to the back stack
            .commit() // Commit the transaction
    }

    // Clean up binding object when the fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

