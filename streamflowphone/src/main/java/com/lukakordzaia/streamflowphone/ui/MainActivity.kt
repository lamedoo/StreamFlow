package com.lukakordzaia.streamflowphone.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lukakordzaia.streamflowphone.R
import com.lukakordzaia.streamflowphone.databinding.ActivityMainBinding
import com.lukakordzaia.streamflowphone.ui.baseclasses.BaseActivity
import com.lukakordzaia.streamflowphone.ui.catalogue.CatalogueFragment
import com.lukakordzaia.streamflowphone.ui.catalogue.cataloguedetails.SingleCatalogueFragment
import com.lukakordzaia.streamflowphone.ui.home.HomeFragment
import com.lukakordzaia.streamflowphone.ui.home.toplistfragment.TopListFragment
import com.lukakordzaia.streamflowphone.ui.phonesingletitle.PhoneSingleTitleFragment
import com.lukakordzaia.streamflowphone.ui.phonesingletitle.tvshowdetailsbottomsheet.TvShowBottomSheetFragment
import com.lukakordzaia.streamflowphone.ui.phonewatchlist.PhoneWatchlistFragment
import com.lukakordzaia.streamflowphone.ui.profile.ProfileFragment
import com.lukakordzaia.streamflowphone.ui.searchtitles.SearchTitlesFragment
import com.lukakordzaia.streamflowphone.ui.videoplayer.VideoPlayerFragment

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        super.onCreate(savedInstanceState)

        val appToolbar: MaterialToolbar = findViewById(R.id.app_main_toolbar)
        setSupportActionBar(appToolbar)

        setUpNavigation()
        observeFragments()

        sharedPreferences.saveRefreshContinueWatching(false)
    }

    private fun setUpNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fr_nav_host) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_main_bottom)
        bottomNavigationView.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.categoriesFragment, R.id.phoneFavoritesFragment,  R.id.searchTitlesFragment)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun observeFragments() {
        supportFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
                when (f) {
                    is HomeFragment,
                    is CatalogueFragment,
                    is SearchTitlesFragment, is PhoneWatchlistFragment -> {
                        showBottomNavigation()
                    }
                    is ProfileFragment,
                    is TopListFragment,
                    is SingleCatalogueFragment,
                    is PhoneSingleTitleFragment,
                    is TvShowBottomSheetFragment, is VideoPlayerFragment -> {
                        hideBottomNavigation()
                    }
                }
            }
        }, true)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    private fun hideBottomNavigation() {
        with(binding.navMainBottom) {
            if (visibility == View.VISIBLE && alpha == 1f) {
                animate()
                    .alpha(0f)
                    .withEndAction { visibility = View.GONE }
            }
        }
    }

    private fun showBottomNavigation() {
        with(binding.navMainBottom) {
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
        }
    }
}