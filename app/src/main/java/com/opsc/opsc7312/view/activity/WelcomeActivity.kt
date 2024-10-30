package com.opsc.opsc7312.view.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.opsc.opsc7312.databinding.ActivityWelcomeBinding
import com.opsc.opsc7312.view.custom.BiometricAuth

class WelcomeActivity : AppCompatActivity() { // Class representing the welcome screen of the application
    // View binding for the activity layout, ensuring type-safe access to views
    private lateinit var binding: ActivityWelcomeBinding

    // Called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Call the superclass method


        // Inflate the layout using view binding and set it as the content view
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set a click listener on the continue button
        binding.continueBtn.setOnClickListener {
            // Start the LoginActivity when the button is clicked
            startActivity(Intent(this, LoginActivity::class.java))
            // Close the WelcomeActivity so it cannot be returned to
            finish()
        }
    }
}
