package com.opsc.opsc7312.view.custom

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.view.activity.LoginActivity
import com.opsc.opsc7312.view.activity.WelcomeActivity
import java.util.concurrent.Executor

class BiometricAuth {

    // TokenManager for managing authentication tokens
    private lateinit var tokenManager: TokenManager

    // Executor for handling biometric prompt tasks on the main thread
    private lateinit var executor: Executor
    // BiometricPrompt object for handling fingerprint authentication
    private lateinit var biometricPrompt: BiometricPrompt
    // Configuration for the biometric prompt
    private lateinit var promptInfo: PromptInfo

    // Initializes and loads fingerprint authentication settings
    // Accepts an AppCompatActivity to allow flexibility for multiple activities
    fun loadFingerprint(activity: AppCompatActivity) {
        // Initialize TokenManager instance
        tokenManager = TokenManager.getInstance(activity)

        // Set the executor to run tasks on the main thread
        executor = ContextCompat.getMainExecutor(activity)

        // Configure the biometric prompt and set callbacks for authentication results
        biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                // Callback when there is an error in the authentication process
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Show a toast message to inform the user of the authentication error
                    Toast.makeText(activity, "Auth error $errString", Toast.LENGTH_SHORT).show()
                }

                // Callback when authentication is successful
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // If the current activity is LoginActivity, navigate to MainActivity
                    if (activity is LoginActivity) {
                        activity.startActivity(Intent(activity, MainActivity::class.java))
                        activity.finish() // End the LoginActivity to prevent returning to it
                    }
                }

                // Callback when authentication fails
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Show a toast message to inform the user of the authentication failure
                    Toast.makeText(activity, "Auth failed", Toast.LENGTH_SHORT).show()
                }
            }
        )

        // Configure the prompt information for the biometric dialog
        promptInfo = PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Fingerprint authentication required")
            .setNegativeButtonText("Cancel")
            .build()

        // Retrieve the user's authentication token
        val token = tokenManager.getToken()

        // Check if the token is available (user is logged in)
        if (token != null) {
            // Access shared preferences to check if biometric authentication is enabled
            val securityPref = activity.getSharedPreferences("SecurityPreferences", Context.MODE_PRIVATE)
            val isFingerprintOn = securityPref.getBoolean("biometric_enabled", false)

            // If biometric authentication is enabled, prompt the user for fingerprint authentication
            if (isFingerprintOn) {
                biometricPrompt.authenticate(promptInfo)
            }
        } else {
            // If no token is available and the user is on the WelcomeActivity, redirect to LoginActivity

            if (activity is WelcomeActivity) {
                activity.startActivity(Intent(activity, LoginActivity::class.java))
            }
        }
    }
}