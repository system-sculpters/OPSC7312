package com.opsc.opsc7312.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.ActivityRegisterBinding
import com.opsc.opsc7312.model.api.controllers.AuthController
import com.opsc.opsc7312.model.data.model.User
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: AuthController
    //private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = ViewModelProvider(this).get(AuthController::class.java)

        binding.btnSignIn.setOnClickListener { registerUser() }

        binding.tvSignUpPrompt.setOnClickListener{
            redirectToLogin()
        }
    }

    private fun registerUser() {
        val username = binding.etUsername.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etUsername.text.toString()

        auth.status.observe(this)  { status ->
            // Handle status changes (success or failure)
            if (status) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                redirectToLogin()
            } else {
                Toast.makeText(this, "Registration unsuccessful", Toast.LENGTH_SHORT).show()
            }
        }

        auth.message.observe(this) { message ->
            // Show message to the user, if needed
            Log.d("Registration message", message)
        }


        val user = User(username = username, email = email, password = password)
        // Example API calls
        auth.register(user)
    }

    private fun redirectToLogin(){
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}