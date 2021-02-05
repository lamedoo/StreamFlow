package com.lukakordzaia.imoviesapp.ui.phone

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.ui.phone.genres.GenresFragment
import com.lukakordzaia.imoviesapp.ui.phone.genres.singlegenre.SingleGenreFragment
import com.lukakordzaia.imoviesapp.ui.phone.home.HomeFragment
import com.lukakordzaia.imoviesapp.ui.phone.searchtitles.SearchTitlesFragment
import com.lukakordzaia.imoviesapp.ui.phone.settings.SettingsFragment
import com.lukakordzaia.imoviesapp.ui.phone.singletitle.SingleTitleFragment
import com.lukakordzaia.imoviesapp.ui.phone.singletitle.choosetitledetails.ChooseTitleDetailsFragment
import com.lukakordzaia.imoviesapp.ui.phone.singletitle.tabs.SingleTitleFragmentInfo
import com.lukakordzaia.imoviesapp.ui.phone.singletitle.tabs.SingleTitleFragmentSimilar
import com.lukakordzaia.imoviesapp.ui.phone.videoplayer.VideoPlayerFragment
import com.lukakordzaia.imoviesapp.utils.setGone
import com.lukakordzaia.imoviesapp.utils.setVisible
import com.lukakordzaia.imoviesapp.utils.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            setBottomNav()
        }

        val appToolbar: MaterialToolbar = findViewById(R.id.app_main_toolbar)
        setSupportActionBar(appToolbar)

        supportFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
                when (f) {
                    is HomeFragment, is GenresFragment, is SearchTitlesFragment -> {
                        showBottomNavigation()
                    }
                    is SingleTitleFragment, is SingleTitleFragmentInfo, is SingleTitleFragmentSimilar, is ChooseTitleDetailsFragment, is SettingsFragment, is SingleGenreFragment -> {
                        hideBottomNavigation()
                    }
                    is VideoPlayerFragment -> {
                        app_main_toolbar.setGone()
                        hideBottomNavigation()
                    }
                    else -> {
                        app_main_toolbar.setVisible()
//                        hideBottomNavigation()
                    }
                }
            }
        }, true)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setBottomNav()
    }

    private fun setBottomNav() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_main_bottom)
        val navGraphIds = listOf(R.navigation.home_fragment_nav, R.navigation.genres_fragment_nav, R.navigation.search_fragment_nav)
        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.homeFragment,
                R.id.genresFragment,
                R.id.searchTitlesFragment,
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
        return currentNavController?.value?.navigateUp() ?: false
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