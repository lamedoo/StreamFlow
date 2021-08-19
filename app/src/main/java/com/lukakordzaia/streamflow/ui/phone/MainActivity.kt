package com.lukakordzaia.streamflow.ui.phone

//import com.lukakordzaia.streamflow.utils.setupWithNavController
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.ui.phone.catalogue.CatalogueFragment
import com.lukakordzaia.streamflow.ui.phone.catalogue.cataloguedetails.singlegenre.SingleGenreFragment
import com.lukakordzaia.streamflow.ui.phone.catalogue.cataloguedetails.singlestudio.SingleStudioFragment
import com.lukakordzaia.streamflow.ui.phone.favorites.PhoneFavoritesFragment
import com.lukakordzaia.streamflow.ui.phone.home.HomeFragment
import com.lukakordzaia.streamflow.ui.phone.home.toplistfragments.NewMoviesFragment
import com.lukakordzaia.streamflow.ui.phone.home.toplistfragments.TopMoviesFragment
import com.lukakordzaia.streamflow.ui.phone.home.toplistfragments.TopTvShowsFragment
import com.lukakordzaia.streamflow.ui.phone.phonesingletitle.PhoneSingleTitleFragment
import com.lukakordzaia.streamflow.ui.phone.phonesingletitle.tvshowdetailsbottomsheet.TvShowBottomSheetFragment
import com.lukakordzaia.streamflow.ui.phone.profile.ProfileFragment
import com.lukakordzaia.streamflow.ui.phone.searchtitles.SearchTitlesFragment
import com.lukakordzaia.streamflow.ui.phone.videoplayer.VideoPlayerFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val appToolbar: MaterialToolbar = findViewById(R.id.app_main_toolbar)
        setSupportActionBar(appToolbar)

        setUpNavigation()
        observeFragments()
    }

    private fun setUpNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.fr_nav_host
        ) as NavHostFragment
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
                    is SearchTitlesFragment, is PhoneFavoritesFragment -> {
                        showBottomNavigation()
                    }
                    is ProfileFragment,
                    is SingleGenreFragment,
                    is TopMoviesFragment,
                    is TopTvShowsFragment,
                    is NewMoviesFragment,
                    is SingleStudioFragment,
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