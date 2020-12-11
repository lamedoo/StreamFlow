package com.lukakordzaia.imoviesapp.ui

import android.os.Bundle
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.transition.Slide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.ui.singlemovie.SingleMovieFragment
import com.lukakordzaia.imoviesapp.ui.singlemovie.choosemoviedetails.ChooseMovieDetailsFragment
import com.lukakordzaia.imoviesapp.ui.videoplayer.VideoPlayerFragment
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


        supportFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
                when (f) {
                    is SingleMovieFragment -> hideBottomNavigation()
                    is ChooseMovieDetailsFragment -> hideBottomNavigation()
                    is VideoPlayerFragment -> hideBottomNavigation()
                    else -> showBottomNavigation()
                }
            }
        }, true)
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController.value?.navigateUp() ?: false || super.onSupportNavigateUp()
    }

    private fun hideBottomNavigation() {
        with(nav_main_bottom) {
            if (visibility == View.VISIBLE && alpha == 1f) {
                animate()
                    .alpha(0f)
                    .withEndAction { visibility = View.GONE }
            }
        }
    }

    private fun showBottomNavigation() {
        with(nav_main_bottom) {
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
        }
    }
}