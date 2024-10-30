package com.opsc.opsc7312.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.ActivityRegisterBinding
import com.opsc.opsc7312.model.api.controllers.AuthController
import com.opsc.opsc7312.model.data.model.User
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.view.custom.TimeOutDialog

class RegisterActivity : AppCompatActivity() {
    // View binding for the activity layout, providing type-safe access to views
    private lateinit var binding: ActivityRegisterBinding
    // AuthController instance for managing user authentication
    private lateinit var auth: AuthController
    // Dialog for showing timeout messages and progress
    private lateinit var timeOutDialog: TimeOutDialog

    // Request code for Google Sign-In
    private val RC_SIGN_IN = 9001

    // Firebase Authentication instance

    // These variable were adapted from YouTube
    // https://youtu.be/suVgcrPwYKQ?si=2FCFY8EXmnnaZuh0
    // Easy Tuto
    // https://www.youtube.com/@EasyTuto1
    private val firebaseAuth = FirebaseAuth.getInstance()
    // Google Sign-In client for managing Google sign-in
    private lateinit var googleSignInClient: GoogleSignInClient
    // Variable to store error messages during validation
    private var errorMessage = ""

    // Called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Call the superclass method

        // Inflate the layout using view binding and set it as the content view
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the AuthController for user authentication
        auth = ViewModelProvider(this).get(AuthController::class.java)

        // Configure Google Sign-In options

        // These variable were adapted from YouTube
        // https://youtu.be/suVgcrPwYKQ?si=2FCFY8EXmnnaZuh0
        // Easy Tuto
        // https://www.youtube.com/@EasyTuto1
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Token for Firebase
            .requestEmail() // Request the user's email
            .build()

        // Initialize the Google Sign-In client with the options
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Set a click listener for the sign-in button to register the user
        binding.btnSignIn.setOnClickListener { registerUser() }

        // Set a click listener to redirect to the LoginActivity
        binding.tvSignUpPrompt.setOnClickListener {
            redirectToLogin()
        }

        // Set a click listener for Google login
        binding.googleLogin.setOnClickListener {
            signInWithGoogle()
        }

        // Initialize the timeout dialog
        timeOutDialog = TimeOutDialog()
    }

    // Method to register the user
    private fun registerUser() {
        // Show a progress dialog while registering
        val progressDialog = timeOutDialog.showProgressDialog(this)

        // Retrieve user input from the UI
        val username = binding.etUsername.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etUsername.text.toString() // Should be password field instead

        // Validate the input fields
        if (!validateInput(username, email, password)) {
            progressDialog.dismiss() // Dismiss the dialog on validation failure
            timeOutDialog.showAlertDialog(this, errorMessage) // Show alert for errors
            errorMessage = "" // Reset error message
            return
        }

        // Create a new User object with the provided details
        val user = User(username = username, email = email, password = password)

        // Observe the authentication status for registration success or failure
        auth.status.observe(this) { status ->
            // Handle registration status changes (success or failure)
            if (status) {
                // Update the progress dialog for successful registration
                timeOutDialog.updateProgressDialog(this, progressDialog, getString(R.string.registration_successful), hideProgressBar = true)

                // Dismiss the dialog after 2 seconds and redirect to the login screen
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss() // Dismiss the dialog
                    redirectToLogin() // Redirect to LoginActivity
                }, 2000)
            } else {
                // Update the progress dialog for unsuccessful registration
                timeOutDialog.updateProgressDialog(this, progressDialog, getString(R.string.user_exists), hideProgressBar = true)

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss() // Dismiss the dialog
                }, 2000)
            }
        }

        // Observe error messages related to registration
        auth.message.observe(this) { message ->
            // Handle error messages and show timeout dialog if necessary
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                progressDialog.dismiss() // Dismiss the dialog on error
                timeOutDialog.showTimeoutDialog(this) {
                    timeOutDialog.showProgressDialog(this) // Show progress dialog again
                    timeOutDialog.updateProgressDialog(this, progressDialog, getString(R.string.connecting), hideProgressBar = false) // Update progress dialog
                    auth.register(user) // Attempt to register the user again
                }
            }
        }

        // Call the register method to initiate the login process
        auth.register(user)
    }

    // Method to validate user input before registration
    private fun validateInput(username: String, email: String, password: String): Boolean {
        if (username.isBlank()) {
            errorMessage += "${getString(R.string.empty_username)}\n" // Error if username is empty
        }

        if (email.isEmpty()) {
            errorMessage += "${getString(R.string.empty_email)}\n"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage += "${getString(R.string.invalid_email_format)}\n"
        }

        if (password.isBlank()){
            errorMessage += "${getString(R.string.blank_password)}\n"
        }

        // Return true if there are no errors, otherwise false
        return errorMessage == ""
    }

    // Method to redirect to the login activity
    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java)) // Start LoginActivity
        finish() // Finish current activity
    }

    // Method to initiate Google sign-in process
    fun signInWithGoogle() {
        // Theis method were adapted from YouTube
        // https://youtu.be/suVgcrPwYKQ?si=2FCFY8EXmnnaZuh0
        // Easy Tuto
        // https://www.youtube.com/@EasyTuto1

        firebaseAuth.signOut() // Sign out of Firebase to ensure a clean login

        // Sign out from Google account
        googleSignInClient.signOut().addOnCompleteListener {
            // Start Google sign-in intent
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN) // Launch sign-in activity
        }
    }

    // Handle the result from the Google sign-in activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data) // Call the superclass method
        // Theis method were adapted from YouTube
        // https://youtu.be/suVgcrPwYKQ?si=2FCFY8EXmnnaZuh0
        // Easy Tuto
        // https://www.youtube.com/@EasyTuto1

        if (requestCode == RC_SIGN_IN) {
            // Get the sign-in task result
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task) // Handle the result
        }
    }

    // Process the result of Google sign-in
    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        // Theis method were adapted from YouTube
        // https://youtu.be/suVgcrPwYKQ?si=2FCFY8EXmnnaZuh0
        // Easy Tuto
        // https://www.youtube.com/@EasyTuto1

        try {
            // Get the signed-in account details
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!) // Authenticate with Firebase using the Google ID token
        } catch (e: ApiException) {
            // Handle error during sign-in
            Log.d("error", "something went wrong...") // Log the error
            Toast.makeText(this, "${e.statusCode}", Toast.LENGTH_SHORT).show() // Show error message
        }
    }

    // Authenticate with Firebase using Google credentials
    private fun firebaseAuthWithGoogle(idToken: String) {
        // Theis method were adapted from YouTube
        // https://youtu.be/suVgcrPwYKQ?si=2FCFY8EXmnnaZuh0
        // Easy Tuto
        // https://www.youtube.com/@EasyTuto1

        val credential = GoogleAuthProvider.getCredential(idToken, null) // Get Google credentials
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = firebaseAuth.currentUser // Get current user

                    // Send ID token to the backend if user exists
                    if (firebaseUser != null) {
                        firebaseUser.displayName?.let { firebaseUser.email?.let { it1 ->
                            sendTokenToBackend(it, it1) // Send token and email to the backend
                        } }
                    }
                } else {
                    // Handle error during Firebase authentication
                    Toast.makeText(this, "something went wrong:firebaseAuthWithGoogle", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Send the user's token to the backend for further processing
    private fun sendTokenToBackend(username: String, email: String) {
        val user = User(username = username, email = email) // Create a new User object

        // Show a progress dialog while sending the token
        val progressDialog = timeOutDialog.showProgressDialog(this)

        // Observe the authentication status for success or failure
        auth.status.observe(this) { status ->
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            if (status) {
                // Update the progress dialog for successful registration
                timeOutDialog.updateProgressDialog(this, progressDialog, getString(R.string.registration_successful), hideProgressBar = true)

                // Dismiss the dialog after 2 seconds and redirect to login
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss() // Dismiss the dialog
                    redirectToLogin() // Redirect to LoginActivity
                }, 2000)
            } else {
                // Update the progress dialog for unsuccessful registration
                timeOutDialog.updateProgressDialog(this, progressDialog, getString(R.string.registration_failed), hideProgressBar = true)

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss() // Dismiss the dialog
                }, 2000)
            }
        }

        // Observe error messages related to sending the token
        auth.message.observe(this) { message ->
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            // Handle error messages and show timeout dialog if necessary
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                progressDialog.dismiss() // Dismiss the dialog on error
                timeOutDialog.showTimeoutDialog(this) {
                    timeOutDialog.showProgressDialog(this) // Show progress dialog again
                    timeOutDialog.updateProgressDialog(this, progressDialog, getString(R.string.connecting), hideProgressBar = false) // Update progress dialog
                    auth.registerWithSSO(user) // Attempt to register the user again
                }
            }
        }

        // Call the registerWithSSO method to initiate the login process
        auth.registerWithSSO(user)
    }
}
