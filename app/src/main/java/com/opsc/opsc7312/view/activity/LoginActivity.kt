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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = ViewModelProvider(this).get(AuthController::class.java)

        tokenManager = TokenManager.getInstance(this)
        userManager = UserManager.getInstance(this)

        binding.btnSignIn.setOnClickListener { loginUser() }

        binding.biometricLogin.setOnClickListener{ biometricLogin() }

        binding.tvSignUpPrompt.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
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
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()
        Log.d("login", "$username $password")

        val user = User(username = username, password = password)

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
            if(message == "timeout"){
                timeOutDialog.showTimeoutDialog(this ){
                    //progressDialog.show()
                    timeOutDialog.updateProgressDialog(this, progressDialog, "Connecting...", hideProgressBar = false)
                    auth.login(user)
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
        auth.login(user)
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
            updateEyeIcon(R.drawable.baseline_airplanemode_active_24) // Set the eye-on icon
        }

        // Move cursor to the end
        binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)

        // Toggle visibility flag
        isPasswordVisible = !isPasswordVisible
    }

    private fun updateEyeIcon(drawableResId: Int) {
        // Get the drawable (eye icon)
        val eyeIcon: Drawable? = resources.getDrawable(drawableResId, null)

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
}