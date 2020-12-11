package com.lukakordzaia.imoviesapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.utils.setGone
import com.lukakordzaia.imoviesapp.utils.setVisible
import com.lukakordzaia.imoviesapp.utils.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var currentNavController: LiveData<NavController>

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val appToolbar: MaterialToolbar = findViewById(R.id.app_main_toolbar)
        setSupportActionBar(appToolbar)

        setBottomNav()
    }

    private fun setBottomNav() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_main_bottom)
        val navGraphIds = listOf(R.navigation.home_fragment_nav, R.navigation.topmovies_fragment_nav, R.navigation.search_fragment_nav)
        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.homeFragment,
                R.id.topMoviesFragment,
                R.id.searchMoviesFragment,
            )
        )

        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.fr_nav_host,
            intent = intent
        )
        controller.observe(this, Observer { navController ->
            setupActionBarWithNavController(navController, appBarConfiguration)
        })
        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController.value?.navigateUp() ?: false || super.onSupportNavigateUp()
    }

    fun showBottomNavigation() {
        nav_main_bottom.setVisible()
    }

    fun hideBottomNavigation() {
        nav_main_bottom.setGone()
    }
}