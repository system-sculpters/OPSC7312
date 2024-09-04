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
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.ActivityLoginBinding
import com.opsc.opsc7312.databinding.ActivityMainBinding
import com.opsc.opsc7312.model.api.controllers.AuthController
import com.opsc.opsc7312.model.api.controllers.CategoryController
import com.opsc.opsc7312.model.data.model.User
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.observers.CategoriesObserver

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: AuthController
    private lateinit var tokenManager: TokenManager
    private lateinit var userManager: UserManager

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
    }


    private fun loginUser(){
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()
        Log.d("login", "$username $password")
        auth.status.observe(this)  { status ->
            // Handle status changes (success or failure)
            if (status) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Login unsuccessful", Toast.LENGTH_SHORT).show()
            }
        }

        auth.message.observe(this) { message ->
            // Show message to the user, if needed
            Log.d("login message", message)
        }

        auth.userData.observe(this){ user ->
            val tokenExpirationTime = System.currentTimeMillis() + (60 * 60 * 1000) // Token expires in 1 hour

            tokenManager.saveToken(user.token, tokenExpirationTime)

            userManager.saveUser(user)

            Log.d("trans user", user.id)
        }

        val user = User(username = username, password = password)
        // Example API calls
        auth.login(user)
    }

    private fun biometricLogin() {
        TODO("Not yet implemented")
    }
}