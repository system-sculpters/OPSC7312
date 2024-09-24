package com.opsc.opsc7312.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            // Start your main activity
            val intent = Intent(this, MainActivity::class.java)

            // Add flags to clear the task stack and start fresh
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            // Start MainActivity and finish this splash activity
            startActivity(intent)
            finish() // Finish the splash activity to prevent it from showing on back press
        }, 2000) // 2000 milliseconds delay (2 seconds), adjust as needed
    }
}