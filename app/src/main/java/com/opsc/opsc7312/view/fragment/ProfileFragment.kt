package com.opsc.opsc7312.view.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentProfileBinding
import com.opsc.opsc7312.model.api.controllers.UserController
import com.opsc.opsc7312.model.data.model.User
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.custom.TimeOutDialog
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    // Binding for the fragment's view to access UI elements
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // SharedPreferences for storing user-related preferences
    private lateinit var sharedPreferences: SharedPreferences

    // UserManager to manage user data and actions
    private lateinit var userManager: UserManager

    // TokenManager to handle authentication tokens
    private lateinit var tokenManager: TokenManager

    // ViewModel to manage user data in a lifecycle-conscious way
    private lateinit var userViewModel: UserController

    // Dialog for handling session timeouts
    private lateinit var timeOutDialog: TimeOutDialog

    // Inflate the fragment layout and initialize necessary components
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using View Binding
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Initialize SharedPreferences to store user-related settings
        sharedPreferences = requireActivity().getSharedPreferences("User", Context.MODE_PRIVATE)

        // Get instances of UserManager and TokenManager
        userManager = UserManager.getInstance(requireContext())
        tokenManager = TokenManager.getInstance(requireContext())

        // Initialize the ViewModel for user operations
        userViewModel = ViewModelProvider(this).get(UserController::class.java)

        // Initialize the timeout dialog
        timeOutDialog = TimeOutDialog()

        // Load existing profile data into the UI
        loadProfileData()

        // Set up button listeners for interactions
        setUpListeners()

        // Return the root view of the binding
        return binding.root
    }

    // Set up listeners for UI interactions
    private fun setUpListeners() {
        // Get the current user and their token
        val user = userManager.getUser()
        val token = tokenManager.getToken()

        // Check if the token is null (not authenticated)
        if (token == null) {
            // Handle the case where the user is not authenticated (e.g., show a login screen)
        }

        // Set a click listener for the Save button
        binding.btnSave.setOnClickListener {
            if (token != null) {
                // Save the user's profile data if the token is valid
                saveProfileData(token, user.id)
            } else {
                // Handle the case where there is no valid token (e.g., show an error message)
            }
        }

        // Set a click listener for changing the password
        binding.ChangePassword.setOnClickListener {
            showChangePasswordDialog() // Show dialog to change the password
        }
    }

    // Called when the fragment's view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the toolbar title in the MainActivity
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.profile))
    }

    // Load the user's profile data and display it in the UI
    private fun loadProfileData() {
        val user = userManager.getUser() // Get the current user

        // Retrieve user details
        val username = user.username
        val email = user.email
        val password = userManager.getPassword() // Get the stored password

        // Set the retrieved values to the corresponding UI elements
        binding.username.setText(username)
        binding.email.setText(email)

        // Mask the password for display purposes
        binding.password.text = maskPassword(password)
    }

    private fun saveProfileData(token: String, userId: String) {
        // Show a progress dialog to inform the user about the ongoing operation
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        // Retrieve the updated username and email from the UI input fields
        val username = binding.username.text.toString()
        val email = binding.email.text.toString()

        // Create an updated User object with the new information
        val updatedUser = User(id = userId, username = username, email = email)

        // Observe the status of the update operation from the UserViewModel
        userViewModel.status.observe(viewLifecycleOwner) { status ->
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (status) {
                // If the update is successful, save the updated user information
                userManager.saveUser(updatedUser)
                // Update the progress dialog to show success message
                timeOutDialog.updateProgressDialog(
                    requireContext(),
                    progressDialog,
                    getString(R.string.profile_update_successful),
                    hideProgressBar = true
                )

                // Dismiss the dialog after a delay of 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss() // Dismiss the dialog after the delay
                }, 2000)

                // Optionally, show a toast message indicating success
                // Toast.makeText(requireContext(), "Category creation successful", Toast.LENGTH_LONG).show()
            } else {
                // If the update fails (e.g., email already in use), show an error message
                timeOutDialog.updateProgressDialog(
                    requireContext(),
                    progressDialog,
                    getString(R.string.already_in_use),
                    hideProgressBar = true
                )

                // Dismiss the dialog after a delay of 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss() // Dismiss the dialog after the delay
                }, 2000)
            }
        }

        // Observe any messages from the ViewModel, particularly for timeout or connection issues
        userViewModel.message.observe(viewLifecycleOwner) { message ->
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                // Show a timeout dialog and attempt to reconnect
                timeOutDialog.showTimeoutDialog(requireContext()) {
                    progressDialog.dismiss() // Dismiss the current progress dialog
                    timeOutDialog.showProgressDialog(requireContext()) // Show a new progress dialog
                    timeOutDialog.updateProgressDialog(
                        requireContext(),
                        progressDialog,
                        getString(R.string.connecting),
                        hideProgressBar = false
                    )
                    // Retry updating the email and username
                    userViewModel.updateEmailAndUsername(token, userId, updatedUser)
                }
            }
        }

        // Initiate the update of email and username in the ViewModel
        userViewModel.updateEmailAndUsername(token, userId, updatedUser)
    }

    private fun showChangePasswordDialog() {
        // This method was adapted from eeksforgeeks
        // https://www.geeksforgeeks.org/how-to-create-an-alert-dialog-box-in-android/
        // naved_alam
        // https://www.geeksforgeeks.org/user/naved_alam/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user

        // Retrieve the currently logged-in user and token for authentication
        val user = userManager.getUser()
        val token = tokenManager.getToken()

        // Inflate the custom dialog layout for changing the password
        val dialogView = LayoutInflater.from(context).inflate(R.layout.change_password_dialog, null)

        // Retrieve UI elements from the dialog layout
        val closeBtn: ImageView = dialogView.findViewById(R.id.closeView)
        val newPassword: EditText = dialogView.findViewById(R.id.newPassword)
        val confirmPassword: EditText = dialogView.findViewById(R.id.confirmPassword)
        val saveBtn: Button = dialogView.findViewById(R.id.btnSave)

        // Create an AlertDialog with the inflated view and make it non-cancelable
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false) // Prevent dismissal by tapping outside
            .create()

        // Set the close button to dismiss the dialog when clicked
        closeBtn.setOnClickListener {
            dialog.dismiss()
        }

        // Set the save button's click listener to handle saving the new password
        saveBtn.setOnClickListener {
            // Call method to save the new password with the input values
            if (token != null) {
                saveNewPassword(token, user.id, newPassword.text.toString(), confirmPassword.text.toString())
            } else {

            }
        }
        // Show the dialog to the user
        dialog.show()
    }

    private fun saveNewPassword(token: String, userId: String, newPassword: String, confirmPassword: String) {
        // Show a progress dialog to inform the user about the ongoing password update process
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        // Check if the new password and confirmation match
        if (newPassword != confirmPassword) {
            progressDialog.dismiss() // Dismiss the progress dialog

            // Show an alert dialog to inform the user of the mismatch
            timeOutDialog.showAlertDialog(requireContext(), getString(R.string.confirm_password_not_match))

            return // Exit the method to prevent further processing
        }

        // Observe the status of the password update operation from the UserViewModel
        userViewModel.status.observe(viewLifecycleOwner) { status ->
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (status) {
                // If the update is successful, save the new password
                userManager.savePassword(newPassword)
                // Update the progress dialog to show a success message
                timeOutDialog.updateProgressDialog(
                    requireContext(),
                    progressDialog,
                    getString(R.string.password_update_successful),
                    hideProgressBar = true
                )

                // Dismiss the dialog after a delay of 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss() // Dismiss the dialog after the delay
                }, 2000)
            } else {
                // If the update fails, show an error message
                timeOutDialog.updateProgressDialog(
                    requireContext(),
                    progressDialog,
                    getString(R.string.password_update_failed),
                    hideProgressBar = true
                )

                // Dismiss the dialog after a delay of 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss() // Dismiss the dialog after the delay
                }, 2000)
            }
        }

        // Observe any messages from the ViewModel, particularly for timeout or connection issues
        userViewModel.message.observe(viewLifecycleOwner) { message ->
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                // Show a timeout dialog and attempt to reconnect
                timeOutDialog.showTimeoutDialog(requireContext()) {
                    progressDialog.dismiss() // Dismiss the current progress dialog
                    timeOutDialog.showProgressDialog(requireContext()) // Show a new progress dialog
                    timeOutDialog.updateProgressDialog(
                        requireContext(),
                        progressDialog,
                        getString(R.string.connecting),
                        hideProgressBar = false
                    )
                    // Retry updating the email and username
                    userViewModel.updatePassword(token, userId, newPassword)
                }
            }
        }

        // If a token is available, initiate the password update in the ViewModel
        userViewModel.updatePassword(token, userId, newPassword)

    }

    private fun maskPassword(password: String): String {
        // Replace each character in the password with '*' for security
        return "*".repeat(password.length)
    }

    // Clean up binding object when the fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
