package com.opsc.opsc7312

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.opsc.opsc7312.databinding.ActivityMainBinding
import com.opsc.opsc7312.model.api.controllers.AuthController
import com.opsc.opsc7312.model.data.model.User
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.activity.WelcomeActivity
import com.opsc.opsc7312.view.fragment.AnalyticsFragment
import com.opsc.opsc7312.view.fragment.CategoriesFragment
import com.opsc.opsc7312.view.fragment.CreateTransactionFragment
import com.opsc.opsc7312.view.fragment.GoalsFragment
import com.opsc.opsc7312.view.fragment.HomeFragment
import com.opsc.opsc7312.view.fragment.SettingsFragment
import com.opsc.opsc7312.view.fragment.TransactionsFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // Binding object for activity_main layout to access UI elements
    private lateinit var binding: ActivityMainBinding

    // DrawerLayout to manage the navigation drawer
    private lateinit var drawerLayout: DrawerLayout

    // NavigationView to handle navigation item selection
    private lateinit var navigationView: NavigationView

    // SharedPreferences for storing user preferences
    private lateinit var sharedPreferences: SharedPreferences

    // TokenManager to manage user authentication tokens
    private lateinit var tokenManager: TokenManager

    // UserManager to manage user details
    private lateinit var userManager: UserManager

    // AuthController to handle authentication-related actions
    private lateinit var auth: AuthController

    // FirebaseAuth instance for Firebase authentication
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Load the selected theme before setting the content view
        loadAndApplyTheme()

        // Set the app theme
        setTheme(R.style.Theme_OPSC7312)

        super.onCreate(savedInstanceState)

        // Inflate the binding layout and set it as the content view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Determine if the device is in dark mode to tint icons accordingly
        // https://medium.com/naukri-engineering/implement-dark-theme-support-for-android-application-using-kotlin-665060d269b6
        // Nitin Berwal
        // https://medium.com/@nitinberwal89
        // This dark mode implementation was adapted from mdeium
        val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        tintIconForDarkMode(findViewById(R.id.back_button), isDarkMode)
        tintIconForDarkMode(findViewById(R.id.nav_drawer_opener), isDarkMode)

        // Initialize the DrawerLayout and NavigationView
        drawerLayout = findViewById<DrawerLayout>(R.id.main)
        navigationView = findViewById<NavigationView>(R.id.nav_view)

        // Initialize managers for token and user management
        tokenManager = TokenManager.getInstance(this)
        userManager = UserManager.getInstance(this)
        auth = ViewModelProvider(this).get(AuthController::class.java)

        // Check if the user is logged in and set up navigation accordingly
        if (isLoggedIn()) {
            setupBottomNavigation()  // Initialize bottom navigation
            setupNavigationView()    // Initialize navigation drawer
        } else {
            navigateToWelcome()      // Redirect to welcome screen
        }

        // Set click listener for the back button to navigate back
        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            onBackPressed()
        }

        // Set click listener for the navigation drawer opener
        findViewById<ImageButton>(R.id.nav_drawer_opener).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END) // Open the navigation drawer
        }

        // Set click listener for the Floating Action Button (FAB) to open transaction creation
        binding.fab.setOnClickListener {
            // Handle FAB click, e.g., open GoalsFragment
            changeCurrentFragment(CreateTransactionFragment(), "Create Transaction")
        }
    }

    // Loads the theme preference from SharedPreferences and applies it
    private fun loadAndApplyTheme() {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
        // Harneet Kaur
        // https://stackoverflow.com/users/1444525/harneet-kaur
        // Ziem
        // https://stackoverflow.com/posts/11027631/revisions

        // Initialize SharedPreferences
        sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this)

        // Fetch the saved theme preference, default to "Light" if not found
        val savedTheme = sharedPreferences.getString("theme_preference", "Light") ?: "Light"

        // Apply the saved theme based on user preference
        when (savedTheme) {
            "Light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "Automatic" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    // Sets up bottom navigation for fragment switching
    private fun setupBottomNavigation() {
        // Start with the HomeFragment as the initial fragment
        changeCurrentFragment(HomeFragment(), "Home")

        // Set the listener for bottom navigation item selections
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> changeCurrentFragment(HomeFragment(), "Home")
                R.id.transactions -> changeCurrentFragment(TransactionsFragment(), "Transactions")
                R.id.analytics -> changeCurrentFragment(AnalyticsFragment(), "Analytics")
                R.id.settings -> changeCurrentFragment(SettingsFragment(), "Settings")
            }
            true
        }
    }

    // Changes the current displayed fragment and updates the toolbar title
    private fun changeCurrentFragment(fragment: Fragment, title: String) {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/52318195/how-to-change-fragment-kotlin
        // Marcos Maliki
        // https://stackoverflow.com/users/8108169/marcos-maliki
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, fragment) // Replace the current fragment
            commit() // Commit the transaction
        }
        setToolbarTitle(title) // Update the toolbar title
    }

    // Sets the title of the toolbar
    fun setToolbarTitle(title: String) {
        binding.toolbarTitle.text = title
    }

    // Initializes the navigation view and sets up its listener
    private fun setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(this)
    }

    // Handles navigation item selections from the navigation drawer
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_investment -> changeCurrentFragment(CategoriesFragment(), "Investments")
            R.id.nav_categories -> changeCurrentFragment(CategoriesFragment(), "Categories")
            R.id.nav_goal -> changeCurrentFragment(GoalsFragment(), "Goals")
            R.id.nav_logout -> {
                logOut() // Handle logout action
            }
        }
        drawerLayout.closeDrawer(GravityCompat.END) // Close the drawer after selection
        return true
    }

    // Tints the icon based on the current theme (dark/light mode)
    private fun tintIconForDarkMode(imageButton: ImageButton, isDarkMode: Boolean) {
        // Determine the appropriate color based on the current mode
        val color = if (isDarkMode) {
            getColor(R.color.white)  // Use white for dark mode
        } else {
            getColor(R.color.dark_grey)  // Use dark grey for light mode
        }
        imageButton.setColorFilter(color) // Apply the color filter to the icon
    }

    // Navigates to the authentication screens if the user is not logged in
    private fun navigateToWelcome() {
        startActivity(Intent(this, WelcomeActivity::class.java)) // Start WelcomeActivity
        finish() // Finish the current activity
    }

    // Checks if the user is currently logged in
    private fun isLoggedIn(): Boolean {
        val token = tokenManager.getToken() // Retrieve the authentication token
        val expirationTime = tokenManager.getTokenExpirationTime() // Get the token expiration time
        return token != null && !AppConstants.isTokenExpired(expirationTime) // Check if the token is valid
    }

    // Logs the user out by clearing tokens and signing out
    private fun logOut() {
        tokenManager.clearToken() // Clear the stored token
        userManager.clearUser() // Clear user details
        firebaseAuth.signOut() // Sign out from Firebase
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show() // Show logout message
        startActivity(Intent(this, MainActivity::class.java)) // Restart the MainActivity
        finish() // Finish the current activity
    }
}
