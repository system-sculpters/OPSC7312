package com.opsc.opsc7312

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.opsc.opsc7312.databinding.ActivityMainBinding
import com.opsc.opsc7312.model.data.Category
import com.opsc.opsc7312.view.fragment.CategoriesFragment
import com.opsc.opsc7312.view.fragment.Category_mFragment
import com.opsc.opsc7312.view.fragment.CreateGoalFragment
import com.opsc.opsc7312.view.fragment.GoalsFragment
import com.opsc.opsc7312.view.fragment.HomeFragment

import com.opsc.opsc7312.view.fragment.Transaction_mFragment
import com.opsc.opsc7312.view.fragment.TransactionsFragment


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding

    private lateinit var drawerLayout: DrawerLayout

    //private lateinit var toolbar: Toolbar

    private lateinit var  navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        tintIconForDarkMode(findViewById(R.id.back_button), isDarkMode)
        tintIconForDarkMode(findViewById(R.id.nav_drawer_opener), isDarkMode)


        drawerLayout = findViewById<DrawerLayout>(R.id.main)

        navigationView = findViewById<NavigationView>(R.id.nav_view)

        //toolbar = findViewById<Toolbar>(R.id.nav_toolbar)
        //setSupportActionBar(toolbar)
        setupBottomNavigation()

        setupNavigationView()

        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            onBackPressed()
        }

        // Handle the navigation drawer opener click
        findViewById<ImageButton>(R.id.nav_drawer_opener).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        binding.fab.setOnClickListener {
            changeCurrentFragment(GoalsFragment(), "Add Transaction")
        }
    }

    private fun setupBottomNavigation() {

        changeCurrentFragment(HomeFragment(), "Home")

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> changeCurrentFragment(HomeFragment(), "Home")
                R.id.transactions -> changeCurrentFragment(TransactionsFragment(), "Transactions")
                R.id.add -> changeCurrentFragment(GoalsFragment(), "Add Transaction")
                R.id.analytics -> changeCurrentFragment(CreateGoalFragment(), "Analytics")
                R.id.settings -> changeCurrentFragment(CategoriesFragment(), "Settings")
            }
            true
        }
    }
    private fun changeCurrentFragment(fragment: Fragment, title: String) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, fragment)
            commit()
        }
        // Set the title of the toolbar
        binding.toolbarTitle.text = title
    }


    private fun setupNavigationView() {

        navigationView.setNavigationItemSelectedListener(this)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.nav_investment -> changeCurrentFragment(CategoriesFragment(), "Investments")
            R.id.nav_categories -> changeCurrentFragment(CategoriesFragment(), "Categories")
            R.id.nav_logout -> {
                 Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))

            }
        }
        drawerLayout.closeDrawer(GravityCompat.END)
        return true
    }

    fun tintIconForDarkMode(imageButton: ImageButton, isDarkMode: Boolean) {
        val color = if (isDarkMode) {
            getColor(R.color.white)  // Use white for dark mode
        } else {
            getColor(R.color.black)  // Use black for light mode
        }
        imageButton.setColorFilter(color)
    }

}