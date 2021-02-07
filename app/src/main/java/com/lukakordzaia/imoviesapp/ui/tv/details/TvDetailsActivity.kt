package com.lukakordzaia.imoviesapp.ui.tv.details

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.FragmentActivity
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.ui.tv.details.titledetails.TvDetailsFragment
import com.lukakordzaia.imoviesapp.ui.tv.details.titlefiles.TvTitleFilesFragment
import com.lukakordzaia.imoviesapp.utils.createToast

class TvDetailsActivity : FragmentActivity() {
    private val tvDetailsFragment = TvDetailsFragment()
    private val tvTitleFilesFragment = TvTitleFilesFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_details)

        supportFragmentManager.beginTransaction()
                .add(R.id.tv_details_fr_nav_host, tvDetailsFragment)
                .show(tvDetailsFragment)
                .commit()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                return if (tvDetailsFragment.onKeyDown()) {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.tv_details_fr_nav_host, tvTitleFilesFragment)
                            .show(tvTitleFilesFragment)
                            .commit()
                    true
                } else {
                    super.onKeyDown(keyCode, event)
                }
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                return if (tvTitleFilesFragment.onKeyDown()) {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.tv_details_fr_nav_host, tvDetailsFragment)
                            .show(tvDetailsFragment)
                            .commit()
                    true
                } else {
                    super.onKeyDown(keyCode, event)
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    interface MyKeyEventListener {
        fun onKeyDown(): Boolean
    }
}