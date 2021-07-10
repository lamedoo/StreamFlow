package com.lukakordzaia.streamflow.ui.tv.details

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.ui.tv.details.titledetails.TvDetailsFragment

class TvDetailsActivity : FragmentActivity() {
    private var currentFragment = TITLE_INFO
    private val tvDetailsFragment = TvDetailsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_details)

        supportFragmentManager.beginTransaction()
                .replace(R.id.tv_details_fr_nav_host, tvDetailsFragment)
                .commit()
    }

    override fun onBackPressed() {
        if (currentFragment == TITLE_DETAILS) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_from_top, R.anim.slide_out_down)
                .replace(R.id.tv_details_fr_nav_host, TvDetailsFragment())
                .commit()
            setCurrentFragment(TITLE_INFO)
        } else {
            super.onBackPressed()
        }
    }

    fun setCurrentFragment(fragment: Int) {
        currentFragment = fragment
    }

    fun getCurrentFragment() = currentFragment

    companion object {
        const val TITLE_INFO = 0
        const val TITLE_DETAILS = 1
    }
}