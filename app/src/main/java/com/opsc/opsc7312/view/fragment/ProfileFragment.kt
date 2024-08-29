package com.opsc.opsc7312.view.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.opsc.opsc7312.R

class ProfileFragment : Fragment(), ChangePasswordFragment.ChangePasswordListener {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var tvChangePassword: TextView
    private lateinit var passwordEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize views
        tvChangePassword = view.findViewById(R.id.tvChangePassword)
        passwordEditText = view.findViewById(R.id.etPassword)

        // Set up change password listener
        tvChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        // Load profile data
        loadProfileData()

        return view
    }

    private fun showChangePasswordDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.fragment_change_password, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val closeButton = dialogView.findViewById<ImageButton>(R.id.btnClose)
        val changePasswordButton = dialogView.findViewById<Button>(R.id.btnChangePassword)
        val newPasswordEditText = dialogView.findViewById<EditText>(R.id.etNewPassword)
        val confirmPasswordEditText = dialogView.findViewById<EditText>(R.id.etConfirmPassword)

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        changePasswordButton.setOnClickListener {
            val newPassword = newPasswordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (newPassword == confirmPassword) {
                onPasswordChanged(newPassword)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun loadProfileData() {
        // This is just an example. You should replace it with actual data loading logic
        val usernameEditText: EditText = view?.findViewById(R.id.etUsername) ?: return
        val emailEditText: EditText = view?.findViewById(R.id.etEmail) ?: return

        val username = sharedPreferences.getString("username", "defaultUsername")
        val email = sharedPreferences.getString("email", "user@example.com")
        val password = sharedPreferences.getString("password", "********")

        usernameEditText.setText(username)
        emailEditText.setText(email)
        passwordEditText.setText(password)
    }

    override fun onPasswordChanged(newPassword: String) {
        passwordEditText.setText(newPassword)
        Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show()

        with(sharedPreferences.edit()) {
            putString("password", newPassword)
            apply()
        }
    }

    // Other methods and logic for the ProfileFragment...
}
