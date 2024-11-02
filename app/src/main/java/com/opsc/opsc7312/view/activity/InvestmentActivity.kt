package com.opsc.opsc7312.view.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.ActivityInvestmentBinding
import com.opsc.opsc7312.databinding.ActivityMainBinding
import com.opsc.opsc7312.view.fragment.AnalyticsFragment
import com.opsc.opsc7312.view.fragment.HomeFragment
import com.opsc.opsc7312.view.fragment.PortfolioFragment
import com.opsc.opsc7312.view.fragment.SettingsFragment
import com.opsc.opsc7312.view.fragment.StocksFragment
import com.opsc.opsc7312.view.fragment.TransactionsFragment

class InvestmentActivity : AppCompatActivity() {

    // Binding object for activity_main layout to access UI elements
    private lateinit var binding: ActivityInvestmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityInvestmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            onBackPressed()
        }
    }

    // Sets the title of the toolbar
    fun setToolbarTitle(title: String) {
        binding.toolbarTitle.text = title
    }
    // Sets up bottom navigation for fragment switching
    private fun setupBottomNavigation() {
        // Start with the HomeFragment as the initial fragment
        changeCurrentFragment(PortfolioFragment())

        // Set the listener for bottom navigation item selections
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> changeCurrentFragment(PortfolioFragment())
                R.id.discover -> changeCurrentFragment(StocksFragment())
                R.id.back -> navigateToMain()
            }
            true
        }
    }

    // Changes the current displayed fragment and updates the toolbar title
    private fun changeCurrentFragment(fragment: Fragment) {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/52318195/how-to-change-fragment-kotlin
        // Marcos Maliki
        // https://stackoverflow.com/users/8108169/marcos-maliki
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.investment_frame_layout, fragment) // Replace the current fragment
            commit() // Commit the transaction
        }
    }

    // Navigates to the authentication screens if the user is not logged in
    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java)) // Start WelcomeActivity
        finish() // Finish the current activity
    }
}