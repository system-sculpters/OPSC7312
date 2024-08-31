package com.opsc.opsc7312.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.ActivityLoginBinding
import com.opsc.opsc7312.databinding.ActivityMainBinding
import com.opsc.opsc7312.model.api.controllers.AuthController
import com.opsc.opsc7312.model.api.controllers.CategoryController
import com.opsc.opsc7312.model.data.model.User
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.view.observers.CategoriesObserver

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: AuthController
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = ViewModelProvider(this).get(AuthController::class.java)

        tokenManager = TokenManager.getInstance(this)

        binding.btnSignIn.setOnClickListener { loginUser() }
    }

    private fun loginUser(){
        val username = binding.etUsername.text.toString()
        val password = binding.etUsername.text.toString()

        auth.status.observe(this)  { status ->
            // Handle status changes (success or failure)
            if (status) {
                // Success
            } else {
                // Failure
            }
        }

        auth.message.observe(this) { message ->
            // Show message to the user, if needed
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        auth.token.observe(this){ token ->
            tokenManager.saveToken(token)
        }

        val user = User(username = username, password = password)
        // Example API calls
        auth.login(user)
    }
}