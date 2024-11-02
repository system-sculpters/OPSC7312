package com.opsc.opsc7312.view.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.ActivityLoginBinding
import com.opsc.opsc7312.model.api.controllers.AuthController
import com.opsc.opsc7312.model.data.model.User
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.custom.BiometricAuth
import com.opsc.opsc7312.view.custom.TimeOutDialog

// Activity class for handling user login functionality
class LoginActivity : AppCompatActivity() {
    // View binding for the layout associated with this activity
    private lateinit var binding: ActivityLoginBinding

    // Controllers for handling authentication, token management, and user management
    private lateinit var auth: AuthController
    private lateinit var tokenManager: TokenManager
    private lateinit var userManager: UserManager

    // Flag to toggle password visibility
    private var isPasswordVisible: Boolean = false

    // Dialog for session timeout handling
    private lateinit var timeOutDialog: TimeOutDialog

    // Request code for Google Sign-In
    private val RC_SIGN_IN = 123


    // These variables were adapted from YouTube
    // https://youtu.be/suVgcrPwYKQ?si=2FCFY8EXmnnaZuh0
    // Easy Tuto
    // https://www.youtube.com/@EasyTuto1

    // Firebase authentication instance for handling Firebase auth operations
    private val firebaseAuth = FirebaseAuth.getInstance()

    // Google Sign-In client for handling Google authentication
    private lateinit var googleSignInClient: GoogleSignInClient

    // Variable to store error messages
    private var errorMessage = ""

    // Override the onCreate method to set up the activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge layout for immersive experience
        enableEdgeToEdge()

        // Inflate the activity's layout using view binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel for authentication operations
        auth = ViewModelProvider(this).get(AuthController::class.java)

        // Initialize TokenManager and UserManager for managing tokens and user data
        tokenManager = TokenManager.getInstance(this)
        userManager = UserManager.getInstance(this)


        // Configure Google Sign-In options
        // These variables were adapted from YouTube
        // https://youtu.be/suVgcrPwYKQ?si=2FCFY8EXmnnaZuh0
        // Easy Tuto
        // https://www.youtube.com/@EasyTuto1

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Token for Firebase
            .requestEmail() // Request email address
            .build()

        // Initialize Google Sign-In client with the configured options
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Set up click listener for the Sign In button
        binding.btnSignIn.setOnClickListener { loginUser() }

        // Set up click listener to navigate to the registration activity
        binding.tvSignUpPrompt.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish() // Finish current activity
        }

        // Set up click listener for Google login button
        binding.googleLogin.setOnClickListener {
            signInWithGoogle()
        }

        // Set up touch listener to toggle password visibility
        binding.etPassword.setOnTouchListener { _, event ->
            val drawableEndIndex = 2 // Index for the drawable on the right
            if (event.action == MotionEvent.ACTION_UP) {
                // Check if the touch event is on the password visibility toggle drawable
                if (event.rawX >= (binding.etPassword.right - binding.etPassword.compoundDrawables[drawableEndIndex].bounds.width())) {
                    togglePasswordVisibility() // Toggle password visibility
                    return@setOnTouchListener true // Consume the touch event
                }
            }
            false // Do not consume the touch event
        }

        // Initialize the timeout dialog for session management
        timeOutDialog = TimeOutDialog()

        // Check if the user is logged in and set up navigation accordingly
        // Check if user is logged in, then trigger biometric authentication if enabled
        if (isValidTokenIn()) {
            val biometricAuth = BiometricAuth()
            biometricAuth.loadFingerprint(this)  // Trigger biometric authentication when app is resumed
        }
    }

    // Function to handle user login process
    private fun loginUser() {
        // Show a progress dialog to indicate loading state
        val progressDialog = timeOutDialog.showProgressDialog(this)

        // Get email and password input from the respective EditText fields
        val email = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()

        // Log the email and password for debugging purposes
        Log.d("login", "$email $password")

        // Validate input; if not valid, dismiss the progress dialog and show an alert
        if (!validateInput(email, password)) {
            progressDialog.dismiss() // Dismiss the progress dialog
            timeOutDialog.showAlertDialog(this, errorMessage) // Show error message
            errorMessage = "" // Reset error message
            return // Exit the function if input is invalid
        }

        // Create a User object with the email and password
        val user = User(email = email, password = password)

        // Observe the authentication status
        auth.status.observe(this) { status ->
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            // Handle status changes (success or failure)
            if (status) {
                // Update the progress dialog to indicate success
                timeOutDialog.updateProgressDialog(this, progressDialog, "Login successful!", hideProgressBar = true)

                // Dismiss the dialog after a 2-second delay
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss() // Dismiss the progress dialog
                    // Navigate to MainActivity after login success
                    startActivity(Intent(this, MainActivity::class.java))
                    finish() // Finish the current activity
                }, 2000)
            } else {
                // Update the progress dialog to indicate failure
                timeOutDialog.updateProgressDialog(this, progressDialog, "Login unsuccessful!", hideProgressBar = true)

                // Dismiss the dialog after a 2-second delay
                Handler(Looper.getMainLooper()).postDelayed({
                    progressDialog.dismiss() // Dismiss the progress dialog
                }, 2000)
            }
        }

        // Observe authentication messages, such as connection issues or timeouts
        auth.message.observe(this) { message ->
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            Log.d("message", "message: ${message}")

            // Check for timeout or inability to connect
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                progressDialog.dismiss() // Dismiss the current dialog
                // Show a timeout dialog and attempt to reconnect
                timeOutDialog.showTimeoutDialog(this) {
                    // Restart the progress dialog
                    timeOutDialog.showProgressDialog(this)
                    timeOutDialog.updateProgressDialog(this, progressDialog, getString(R.string.connecting), hideProgressBar = false)
                    auth.login(user) // Attempt to login again
                }
            }
        }

        // Observe user data after successful authentication
        auth.userData.observe(this) { user_data ->
            val tokenExpirationTime = AppConstants.tokenExpirationTime() // Token expires in 2 hours

            // Save the token and user data using the TokenManager and UserManager
            tokenManager.saveToken(user_data.token, tokenExpirationTime)
            userManager.saveUser(user_data)
            userManager.savePassword(password) // Save the user's password (presumably hashed)
        }

        // Perform the login API call
        auth.login(user) // Initiate login process
    }

    private fun validateInput( email: String, password: String): Boolean {
        if (email.isEmpty()) {
            errorMessage += "${getString(R.string.empty_email)}\n"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage += "${getString(R.string.invalid_email_format)}\n"
        }

        if (password.isBlank()){
            errorMessage += "${getString(R.string.blank_password)}\n"
        }

        return errorMessage == ""
    }

    private fun togglePasswordVisibility() {
        // Toggle password visibility
        if (isPasswordVisible) {
            binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            updateEyeIcon(R.drawable.baseline_remove_red_eye_24) // Set the eye-off icon
        } else {
            binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            updateEyeIcon(R.drawable.baseline_remove_red_eye_24) // Set the eye-on icon
        }

        // Move cursor to the end
        binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)

        // Toggle visibility flag
        isPasswordVisible = !isPasswordVisible
    }

    private fun updateEyeIcon(drawableResId: Int) {
        // Get the drawable (eye icon)
        val eyeIcon: Drawable? = resources.getDrawable(drawableResId, null)

        // Convert 24dp to pixels
        val dp24 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, resources.displayMetrics).toInt()

        // Set the size (bounds) of the drawable to 24dp x 24dp
        eyeIcon?.setBounds(0, 0, dp24, dp24)
        // Retrieve the color from the theme attribute `whiteItemBorder`
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.whiteItemBorder, typedValue, true)
        val color = typedValue.data

        // Apply the tint to the drawable
        eyeIcon?.let {
            DrawableCompat.setTint(it, color)
        }

        // Set the drawable to the right of the EditText
        binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, eyeIcon, null)
    }

    // Function to initiate sign-in with Google
    fun signInWithGoogle() {
        // Theis method were adapted from YouTube
        // https://youtu.be/suVgcrPwYKQ?si=2FCFY8EXmnnaZuh0
        // Easy Tuto
        // https://www.youtube.com/@EasyTuto1

        // Sign out from Firebase Auth to ensure a fresh sign-in
        firebaseAuth.signOut()

        // Sign out from Google Sign-In and then start the sign-in intent
        googleSignInClient.signOut().addOnCompleteListener {
            // Create the sign-in intent for Google
            val signInIntent = googleSignInClient.signInIntent
            // Start the sign-in activity and expect a result
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    // Override to handle the result from the sign-in activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Theis method were adapted from YouTube
        // https://youtu.be/suVgcrPwYKQ?si=2FCFY8EXmnnaZuh0
        // Easy Tuto
        // https://www.youtube.com/@EasyTuto1

        // Check if the request code matches the sign-in request
        if (requestCode == RC_SIGN_IN) {
            // Retrieve the sign-in account information from the result
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            // Handle the sign-in result
            handleSignInResult(task)
        }
    }

    // Function to handle the sign-in result from Google Sign-In
    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        // Theis method were adapted from YouTube
        // https://youtu.be/suVgcrPwYKQ?si=2FCFY8EXmnnaZuh0
        // Easy Tuto
        // https://www.youtube.com/@EasyTuto1

        try {
            // Retrieve the signed-in account information
            val account = task.getResult(ApiException::class.java)

            // Get the display name and email from the account
            val displayName = account.displayName!!
            val email = account.email
            Log.d("account", "this is the account $account")

            // Log the account data for debugging purposes
            Log.d("error", "account data:\nusername: $displayName\nemail: $email")

            // Authenticate with Firebase using the Google account's ID token
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Handle any errors that occurred during sign-in
            Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
        }
    }

    // Function to authenticate with Firebase using the Google account's ID token
    private fun firebaseAuthWithGoogle(idToken: String) {
        // Theis method were adapted from YouTube
        // https://youtu.be/suVgcrPwYKQ?si=2FCFY8EXmnnaZuh0
        // Easy Tuto
        // https://www.youtube.com/@EasyTuto1

        // Create a credential using the Google ID token
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        // Sign in with the credential and add a listener to handle the result
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // If sign-in is successful, get the current Firebase user
                    val firebaseUser = firebaseAuth.currentUser

                    // Get the user's email
                    val email = firebaseUser?.email

                    // If the email is not null, send it to the backend
                    if (email != null) {
                        sendTokenToBackend(email)
                    }
                } else {
                    // Handle any errors that occurred during Firebase sign-in
                    Log.d("login error", "this is the account ")
                    Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                }
            }
    }


    // Function to send the user's token to the backend for authentication
    private fun sendTokenToBackend(email: String) {
        // Create a User object with the provided email
        val user = User(email = email)

        // Show a progress dialog to indicate ongoing operation
        val progressDialog = timeOutDialog.showProgressDialog(this)

        // Observe the authentication status to handle success or failure
        auth.status.observe(this) { status ->
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            // Handle status changes (success or failure)
            if (status) {
                // Update the progress dialog to indicate success
                timeOutDialog.updateProgressDialog(this, progressDialog, getString(R.string.login_successful), hideProgressBar = true)

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Dismiss the progress dialog after the delay
                    progressDialog.dismiss()

                    // Navigate to MainActivity upon successful login
                    startActivity(Intent(this, MainActivity::class.java))
                    finish() // Close the current activity
                }, 2000)
            } else {
                // Update the progress dialog to indicate failure
                timeOutDialog.updateProgressDialog(this, progressDialog, getString(R.string.login_fail), hideProgressBar = true)

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Dismiss the progress dialog after the delay
                    progressDialog.dismiss()
                }, 2000)
                // Optionally, show a toast for login failure (commented out)
                // Toast.makeText(this, "Login unsuccessful", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe messages from the authentication process
        auth.message.observe(this) { message ->
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            // Show message to the user if a timeout or network error occurs
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                // Show a timeout dialog and attempt to reconnect
                timeOutDialog.showTimeoutDialog(this) {
                    // Show a progress dialog while connecting
                    timeOutDialog.showProgressDialog(this)
                    timeOutDialog.updateProgressDialog(this, progressDialog, getString(R.string.connecting), hideProgressBar = false)

                    // Attempt to log in with Single Sign-On (SSO) again
                    auth.loginWithSSO(user)
                }
            }
        }

        // Observe user data after successful login
        auth.userData.observe(this) { user_data ->
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            // Get the token expiration time from constants (e.g., 1 hour)
            val tokenExpirationTime = AppConstants.tokenExpirationTime()

            // Save the token and expiration time for later use
            tokenManager.saveToken(user_data.token, tokenExpirationTime)

            // Save user data to local storage
            userManager.saveUser(user_data)
        }

        // Call the loginWithSSO method to initiate the login process
        auth.loginWithSSO(user)
    }

    // Checks if the user is currently logged in
    private fun isValidTokenIn(): Boolean {
        val token = tokenManager.getToken() // Retrieve the authentication token
        val expirationTime = tokenManager.getTokenExpirationTime() // Get the token expiration time
        return token != null && !AppConstants.isTokenExpired(expirationTime) // Check if the token is valid
    }
}