package com.lukakordzaia.streamflow.ui.tv.details

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.ui.tv.details.titledetails.TvDetailsFragment

class TvDetailsActivity : FragmentActivity() {
    private val tvDetailsFragment = TvDetailsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_details)

        supportFragmentManager.beginTransaction()
                .replace(R.id.tv_details_fr_nav_host, tvDetailsFragment)
                .commit()
    }

}