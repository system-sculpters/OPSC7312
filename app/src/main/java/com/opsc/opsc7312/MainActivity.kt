package com.opsc.opsc7312

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.ConnectivityManager
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
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.opsc.opsc7312.databinding.ActivityMainBinding
import com.opsc.opsc7312.model.api.controllers.AuthController
import com.opsc.opsc7312.model.data.model.Goal
import com.opsc.opsc7312.model.data.offline.DatabaseChangeListener
import com.opsc.opsc7312.model.data.offline.dbhelpers.DatabaseHelperProvider
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.model.data.offline.syncworker.GoalSyncWorker
import com.opsc.opsc7312.view.custom.ConnectivityReceiver
import com.opsc.opsc7312.model.data.offline.syncworker.CategorySyncWorker
import com.opsc.opsc7312.model.data.offline.syncworker.TransactionSyncWorker
import com.opsc.opsc7312.view.activity.InvestmentActivity
import com.opsc.opsc7312.view.activity.LoginActivity
import com.opsc.opsc7312.view.custom.NotificationHandler
import com.opsc.opsc7312.view.fragment.AnalyticsFragment
import com.opsc.opsc7312.view.fragment.CategoriesFragment
import com.opsc.opsc7312.view.fragment.CreateTransactionFragment
import com.opsc.opsc7312.view.fragment.GoalsFragment
import com.opsc.opsc7312.view.fragment.HomeFragment
import com.opsc.opsc7312.view.fragment.NotificationListFragment
import com.opsc.opsc7312.view.fragment.SettingsFragment
import com.opsc.opsc7312.view.fragment.TransactionsFragment
import java.util.Locale
import java.util.concurrent.TimeUnit

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

    private lateinit var dbHelperProvider: DatabaseHelperProvider

    private lateinit var notificationHandler: NotificationHandler

    private var isInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // Load the selected theme before setting the content view
        loadAndApplyTheme()

        // Set the app theme
        setTheme(R.style.Theme_OPSC7312)

        // Retrieve the saved language from SharedPreferences
        val sharedPreferences = getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "English")

        // Apply the saved language
        setAppLocale(savedLanguage ?: "English")

        super.onCreate(savedInstanceState)

        // Inflate the binding layout and set it as the content view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelperProvider = DatabaseHelperProvider(this)

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

        notificationHandler = NotificationHandler(this)

        //////////////////////////////////////////////////////////////////////////////////////////////////
        setupBottomNavigation()  // Initialize bottom navigation
        setupNavigationView()    // Initialize navigation drawer

        isInitialized = true

        val connectivityReceiver = ConnectivityReceiver {
            // This implementation was adapted from medium
            // https://medium.com/@dilipsuthar97/listen-to-internet-connection-using-broadcastreceiver-in-android-kotlin-6b527426a6f2
            // Dilip Suthar
            // https://medium.com/@dilipsuthar97
            enqueueImmediateSyncWorker<CategorySyncWorker>(this)
            enqueueImmediateSyncWorker<GoalSyncWorker>(this)
            enqueueImmediateSyncWorker<TransactionSyncWorker>(this)
        }

        this.registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))


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
            changeCurrentFragment(CreateTransactionFragment())
        }
    }

//    override fun onDatabaseChanged() {
//        // Call sync worker to upload local changes
//        if (isInitialized) {
//            // Call sync worker to upload local changes
//            enqueueImmediateSyncWorker<CategorySyncWorker>(this)
//        }
//    }
//
//    override fun onGoalsChanged() {
//        // Call sync worker to upload local changes
//        if (isInitialized) {
//            // Call sync worker to upload local changes
//            enqueueImmediateSyncWorker<GoalSyncWorker>(this)
//        }
//    }
//
//     override fun onTransactionsChanged() {
//         // Call sync worker to upload local changes
//         if (isInitialized) {
//             // Call sync worker to upload local changes
//             enqueueImmediateSyncWorker<TransactionSyncWorker>(this)
//         }
//     }



    private inline fun <reified T : CoroutineWorker> enqueueImmediateSyncWorker(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<T>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(syncRequest)
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
        changeCurrentFragment(HomeFragment())

        // Set the listener for bottom navigation item selections
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> changeCurrentFragment(HomeFragment())
                R.id.transactions -> changeCurrentFragment(TransactionsFragment())
                R.id.analytics -> changeCurrentFragment(AnalyticsFragment())
                R.id.settings -> changeCurrentFragment(SettingsFragment())
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
            replace(R.id.frame_layout, fragment) // Replace the current fragment
            commit() // Commit the transaction
        }
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
            R.id.nav_investment -> {
                startActivity(Intent(this, InvestmentActivity::class.java)) // Start WelcomeActivity
                finish()
            }
            R.id.nav_categories -> changeCurrentFragment(CategoriesFragment())
            //R.id.nav_notification -> changeCurrentFragment(NotificationListFragment())
            R.id.nav_goal -> changeCurrentFragment(GoalsFragment())
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

    // Logs the user out by clearing tokens and signing out
    private fun logOut() {
        tokenManager.clearToken() // Clear the stored token
        userManager.clearUser() // Clear user details
        firebaseAuth.signOut() // Sign out from Firebase
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show() // Show logout message
        startActivity(Intent(this, LoginActivity::class.java)) // Restart the MainActivity
        finish() // Finish the current activity
    }

    private fun setAppLocale(language: String) {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
        // Harneet Kaur
        // https://stackoverflow.com/users/1444525/harneet-kaur
        // Ziem
        // Determine the locale code based on the selected language
        val localeCode = when (language) {
            "Afrikaans" -> "af" // Afrikaans language code
            "Zulu" -> "zu" // Zulu language code
            else -> "en" // Default to English if no match is found
        }

        val locale = Locale(localeCode) // Create a new Locale object
        Locale.setDefault(locale) // Set the default locale

        val config = Configuration() // Create a new Configuration object
        config.setLocale(locale) // Set the locale in the configuration

        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun syncNotification(title: String, message: String){
        notificationHandler.createNotificationChannel()
        notificationHandler.showNotification(title, message)

    }
}
