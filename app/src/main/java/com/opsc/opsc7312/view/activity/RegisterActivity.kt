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
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: AuthController
    private lateinit var timeOutDialog: TimeOutDialog

    private val RC_SIGN_IN = 9001

    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private var errorMessage = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = ViewModelProvider(this).get(AuthController::class.java)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Token for Firebase
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnSignIn.setOnClickListener { registerUser() }

        binding.tvSignUpPrompt.setOnClickListener{
            redirectToLogin()
        }

        binding.googleLogin.setOnClickListener{
            signInWithGoogle()
        }

        timeOutDialog = TimeOutDialog()
    }

    private fun registerUser() {
        val progressDialog = timeOutDialog.showProgressDialog(this)

        val username = binding.etUsername.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etUsername.text.toString()

        if(!validateInput(username, email, password)){
            progressDialog.dismiss()
            timeOutDialog.showAlertDialog(this, errorMessage)
            errorMessage = ""
            return
        }

        val user = User(username = username, email = email, password = password)


        auth.status.observe(this)  { status ->
            // Handle status changes (success or failure)
            if (status) {
                //Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                timeOutDialog.updateProgressDialog(this, progressDialog, "Registration successful!", hideProgressBar = true)

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Dismiss the dialog after the delay
                    progressDialog.dismiss()

                    // Navigate to MainActivity
                    redirectToLogin()
                }, 2000)
            } else {
                timeOutDialog.updateProgressDialog(this, progressDialog, "Registration unsuccessful!", hideProgressBar = true)

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Dismiss the dialog after the delay
                    progressDialog.dismiss()

                }, 2000)
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
                    auth.register(user)
                }
            }
        }

        auth.register(user)
    }

    private fun validateInput(username: String, email: String, password: String): Boolean {
        if (username.isBlank()) {
            errorMessage += "• Username cannot be empty.\n"
        }

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

    private fun redirectToLogin(){
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


    fun signInWithGoogle() {
        firebaseAuth.signOut()

        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
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
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Handle error
            Log.d("error", "something went wrong...")
            Toast.makeText(this, "${e.statusCode}", Toast.LENGTH_SHORT).show()


        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = firebaseAuth.currentUser

                    // Send ID token to the backend
                    if (firebaseUser != null) {
                        firebaseUser.displayName?.let { firebaseUser.email?.let { it1 ->
                            sendTokenToBackend(it,
                                it1
                            )
                        } }
                    }
                } else {
                    // Handle error
                    Toast.makeText(this, "something went wrong:firebaseAuthWithGoogle", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendTokenToBackend(username: String, email: String) {
        val user = User(username = username, email = email)

        val progressDialog = timeOutDialog.showProgressDialog(this)

        auth.status.observe(this) { status ->
            // Handle status changes (success or failure)
            if (status) {
                timeOutDialog.updateProgressDialog(this, progressDialog, "Registration successful!", hideProgressBar = true)

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Dismiss the dialog after the delay
                    progressDialog.dismiss()
                    redirectToLogin()

                }, 2000)
            } else {
                timeOutDialog.updateProgressDialog(this, progressDialog, "Registration unsuccessful!", hideProgressBar = true)

                // Dismiss the dialog after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    // Dismiss the dialog after the delay
                    progressDialog.dismiss()


                }, 2000)
            }
        }

        auth.message.observe(this) { message ->
            // Show message to the user, if needed
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                timeOutDialog.showTimeoutDialog(this) {
                    //progressDialog.show()
                    timeOutDialog.showProgressDialog(this)
                    timeOutDialog.updateProgressDialog(
                        this,
                        progressDialog,
                        "Connecting...",
                        hideProgressBar = false
                    )
                    auth.registerWithSSO(user)
                }
            }
        }

        auth.registerWithSSO(user)
    }
}