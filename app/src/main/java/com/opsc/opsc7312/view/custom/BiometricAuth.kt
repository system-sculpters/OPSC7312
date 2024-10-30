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

    private lateinit var tokenManager: TokenManager


    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: PromptInfo

    fun loadFingerprint(activity: AppCompatActivity) { // Generalized to handle both MainActivity and WelcomeActivity
        tokenManager = TokenManager.getInstance(activity)

        executor = ContextCompat.getMainExecutor(activity)
        biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(activity, "Auth error $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    if (activity is WelcomeActivity) {
                        activity.startActivity(Intent(activity, MainActivity::class.java))
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(activity, "Auth failed", Toast.LENGTH_SHORT).show()
                }
            }
        )

        promptInfo = PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Fingerprint authentication required")
            .setNegativeButtonText("Cancel")
            .build()

        val token = tokenManager.getToken()

        if (token != null) {
            val securityPref = activity.getSharedPreferences("SecurityPreferences", Context.MODE_PRIVATE)
            val isFingerprintOn = securityPref.getBoolean("biometric_enabled", false)

            if (isFingerprintOn) {
                biometricPrompt.authenticate(promptInfo)
            }
        } else {
            if (activity is WelcomeActivity) {
                activity.startActivity(Intent(activity, LoginActivity::class.java))
            }
        }
    }
}