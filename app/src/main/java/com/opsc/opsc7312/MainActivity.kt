package com.opsc.opsc7312

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.opsc.opsc7312.databinding.ActivityMainBinding
import com.opsc.opsc7312.model.data.Category
import com.opsc.opsc7312.view.fragment.CategoriesFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = findViewById<DrawerLayout>(R.id.main)

        toolbar = findViewById<Toolbar>(R.id.nav_toolbar)
        setSupportActionBar(toolbar)
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {

        changeCurrentFragment(CategoriesFragment())

        // Code for when a different button is pressed on the navigation menu
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> changeCurrentFragment(CategoriesFragment())
                //R.id.transactions -> changeCurrentFragment(Home2Fragment())
                //R.id.add -> changeCurrentFragment(Home3Fragment())
                //R.id.analytics -> changeCurrentFragment(Home4Fragment())
                //R.id.settings -> changeCurrentFragment(Home4Fragment())
            }
            true
        }
    }
    private fun changeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, fragment)
            commit()
        }
    }
}