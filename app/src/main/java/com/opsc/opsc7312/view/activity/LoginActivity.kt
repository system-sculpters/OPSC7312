package com.opsc.opsc7312.view.activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
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
import com.opsc.opsc7312.view.custom.TimeOutDialog

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: AuthController
    private lateinit var tokenManager: TokenManager
    private lateinit var userManager: UserManager
    private var isPasswordVisible: Boolean = false
    private lateinit var timeOutDialog: TimeOutDialog
    private val RC_SIGN_IN = 123

    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private var errorMessage = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = ViewModelProvider(this).get(AuthController::class.java)

        tokenManager = TokenManager.getInstance(this)
        userManager = UserManager.getInstance(this)

        // Google Sign-In configuration should be initialized here, where context is available
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Token for Firebase
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnSignIn.setOnClickListener { loginUser() }

        binding.biometricLogin.setOnClickListener{ biometricLogin() }

        binding.tvSignUpPrompt.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        binding.googleLogin.setOnClickListener{
            signInWithGoogle()
        }

        binding.etPassword.setOnTouchListener { _, event ->
            val drawableEndIndex = 2 // Index for the drawable on the right
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.etPassword.right - binding.etPassword.compoundDrawables[drawableEndIndex].bounds.width())) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }

        timeOutDialog = TimeOutDialog()
    }


    private fun loginUser(){
        val progressDialog = timeOutDialog.showProgressDialog(this)
        val email = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()
        Log.d("login", "$email $password")


        if(!validateInput(email, password)){
            progressDialog.dismiss()
            timeOutDialog.showAlertDialog(this, errorMessage)
            errorMessage = ""
            return
        }

        val user = User(email = email, password = password)

        auth.status.observe(this)  { status ->
            // Handle status changes (success or failure)
            if (status) {
                //Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                timeOutDialog.updateProgressDialog(this, progressDialog, "Login successful!", hideProgressBar = true)

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Dismiss the dialog after the delay
                    progressDialog.dismiss()

                    // Navigate to MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }, 2000)
            } else {
                timeOutDialog.updateProgressDialog(this, progressDialog, "Login unsuccessful!", hideProgressBar = true)

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Dismiss the dialog after the delay
                    progressDialog.dismiss()


                }, 2000)
                //Toast.makeText(this, "Login unsuccessful", Toast.LENGTH_SHORT).show()
            }
        }

        auth.message.observe(this) { message ->
            // Show message to the user, if needed

            if(message == "timeout" || message.contains("Unable to resolve host")){
                progressDialog.dismiss()
                timeOutDialog.showTimeoutDialog(this ){
                    //progressDialog.show()
                    timeOutDialog.showProgressDialog(this)
                    timeOutDialog.updateProgressDialog(this, progressDialog, "Connecting...", hideProgressBar = false)
                    auth.login(user)
                }
            }
        }

        auth.userData.observe(this){ user_data ->
            val tokenExpirationTime = System.currentTimeMillis() + (2 * 24 * 60 * 60 * 1000) // Token expires in 2  hour

            tokenManager.saveToken(user_data.token, tokenExpirationTime)

            userManager.saveUser(user_data)

            userManager.savePassword(password)
        }


        // Example API calls
        auth.login(user)
    }

    private fun validateInput( email: String, password: String): Boolean {
        if (email.isEmpty()) {
            errorMessage += "• Email cannot be empty.\n"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage += "• Invalid email format.\n"
        }

        if (password.isBlank()){
            errorMessage += "• Password cannot be empty.\n"
        }

        return errorMessage == ""
    }

    private fun biometricLogin() {
        TODO("Not yet implemented")
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

    fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // Handle the sign-in result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)

            val displayName = account.displayName!!
            val email = account.email
            Log.d("account", "this is the account $account")

            Log.d("error", "account data:\nusername: $displayName\nemail: $email")

            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Handle error
            Toast.makeText(this, "something went wrong...", Toast.LENGTH_SHORT).show()

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = firebaseAuth.currentUser
                    // Send ID token to the backend
                    Toast.makeText(this, "account data:\n" +
                            "username: ${firebaseUser?.email}\n" +
                            "email: ${firebaseUser?.displayName}", Toast.LENGTH_SHORT).show()
                    val email = firebaseUser?.email
                    Log.d("firebaseUser data", "account data:\nusername: ${firebaseUser?.email}\nemail: ${firebaseUser?.displayName}")
                    if (email != null) {
                        sendTokenToBackend(email)
                    }
                } else {
                    // Handle error
                    Log.d("login error", "this is the account ")
                    Toast.makeText(this, "something went wrong...", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendTokenToBackend(email: String) {
        val user = User(email = email)

        val progressDialog = timeOutDialog.showProgressDialog(this)

        auth.status.observe(this)  { status ->
            // Handle status changes (success or failure)
            if (status) {
                //Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                timeOutDialog.updateProgressDialog(this, progressDialog, "Login successful!", hideProgressBar = true)

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Dismiss the dialog after the delay
                    progressDialog.dismiss()

                    // Navigate to MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }, 2000)
            } else {
                timeOutDialog.updateProgressDialog(this, progressDialog, "Login unsuccessful!", hideProgressBar = true)

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Dismiss the dialog after the delay
                    progressDialog.dismiss()


                }, 2000)
                //Toast.makeText(this, "Login unsuccessful", Toast.LENGTH_SHORT).show()
            }
        }

        auth.message.observe(this) { message ->
            // Show message to the user, if needed
            if(message == "timeout" || message.contains("Unable to resolve host")){
                timeOutDialog.showTimeoutDialog(this ){
                    //progressDialog.show()
                    timeOutDialog.showProgressDialog(this)
                    timeOutDialog.updateProgressDialog(this, progressDialog, "Connecting...", hideProgressBar = false)
                    auth.loginWithSSO(user)
                }
            }
        }

        auth.userData.observe(this){ user_data ->
            val tokenExpirationTime = System.currentTimeMillis() + (60 * 60 * 1000) // Token expires in 1 hour

            tokenManager.saveToken(user_data.token, tokenExpirationTime)

            userManager.saveUser(user_data)

            Log.d("trans user", user_data.id)
        }


        // Example API calls
        auth.loginWithSSO(user)
    }
}