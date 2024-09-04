package com.opsc.opsc7312

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.opsc.opsc7312.databinding.ActivityMainBinding
import com.opsc.opsc7312.model.api.controllers.AuthController
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
    private lateinit var binding: ActivityMainBinding

    private lateinit var drawerLayout: DrawerLayout

    //private lateinit var toolbar: Toolbar

    private lateinit var  navigationView: NavigationView

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var  tokenManager: TokenManager

    private lateinit var userManager: UserManager

    private lateinit var auth: AuthController

    override fun onCreate(savedInstanceState: Bundle?) {
        // Load the selected theme before setting the content view
        loadAndApplyTheme()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        tintIconForDarkMode(findViewById(R.id.back_button), isDarkMode)
        tintIconForDarkMode(findViewById(R.id.nav_drawer_opener), isDarkMode)

        drawerLayout = findViewById<DrawerLayout>(R.id.main)
        navigationView = findViewById<NavigationView>(R.id.nav_view)

        tokenManager = TokenManager.getInstance(this)
        userManager = UserManager.getInstance(this)
        auth = ViewModelProvider(this).get(AuthController::class.java)

        if(isLoggedIn()){
            setupBottomNavigation()
            setupNavigationView()
        } else {
            navigateToWelcome()
        }


        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            onBackPressed()
        }

        findViewById<ImageButton>(R.id.nav_drawer_opener).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        binding.fab.setOnClickListener {
            // Handle FAB click, e.g., open GoalsFragment
            changeCurrentFragment(CreateTransactionFragment(), "Create Transaction")
        }
    }

    private fun loadAndApplyTheme() {
        // Initialize SharedPreferences
        sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this)

        // Fetch the saved theme preference, default to "Light" if not found
        val savedTheme = sharedPreferences.getString("theme_preference", "Light") ?: "Light"

        // Apply the saved theme
        when (savedTheme) {
            "Light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "Automatic" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun setupBottomNavigation() {
        changeCurrentFragment(HomeFragment(), "Home")

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> changeCurrentFragment(HomeFragment(), "Home")
                R.id.transactions -> changeCurrentFragment(TransactionsFragment(), "Transactions")
                R.id.add -> changeCurrentFragment(CreateTransactionFragment(), "Add Transaction")
                R.id.analytics -> changeCurrentFragment(AnalyticsFragment(), "Analytics")
                R.id.settings -> changeCurrentFragment(SettingsFragment(), "Settings")
            }
            true
        }
    }

    private fun changeCurrentFragment(fragment: Fragment, title: String) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, fragment)
            commit()
        }
        binding.toolbarTitle.text = title
    }

    private fun setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_investment -> changeCurrentFragment(CategoriesFragment(), "Investments")
            R.id.nav_categories -> changeCurrentFragment(CategoriesFragment(), "Categories")
            R.id.nav_goal -> changeCurrentFragment(GoalsFragment(), "Goals")
            R.id.nav_logout -> {
                logOut()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.END)
        return true
    }

    private fun tintIconForDarkMode(imageButton: ImageButton, isDarkMode: Boolean) {
        val color = if (isDarkMode) {
            getColor(R.color.white)  // Use white for dark mode
        } else {
            getColor(R.color.dark_grey)  // Use black for light mode
        }
        imageButton.setColorFilter(color)
    }

    // Navigate to the authentication screens
    private fun navigateToWelcome() {
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }

    private fun isLoggedIn(): Boolean{
        val token = tokenManager.getToken()
        return token != null
    }

    private fun logOut(){
        val token = tokenManager.getToken()

//        auth.status.observe(this) {
//            status ->
//            if(status){
//                tokenManager.clearToken()
//                userManager.clearUser()
//                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
//                startActivity(Intent(this, MainActivity::class.java))
//            } else {
//                Toast.makeText(this, "Logged out failed", Toast.LENGTH_SHORT).show()
//            }
//        }
//        if (token != null) {
//            auth.logout(token)
//        }
        tokenManager.clearToken()
        userManager.clearUser()
        startActivity(Intent(this, MainActivity::class.java))
    }
}