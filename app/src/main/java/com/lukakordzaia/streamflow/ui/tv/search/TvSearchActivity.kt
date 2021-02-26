package com.lukakordzaia.streamflow.ui.tv.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.helpers.TvCheckFirstItem
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentActivity
import com.lukakordzaia.streamflow.ui.tv.TvActivity
import com.lukakordzaia.streamflow.ui.tv.categories.TvCategoriesActivity
import com.lukakordzaia.streamflow.ui.tv.genres.TvSingleGenreActivity
import com.lukakordzaia.streamflow.utils.AppConstants
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import kotlinx.android.synthetic.main.tv_sidebar.*
import kotlinx.android.synthetic.main.tv_sidebar_collapsed.*

class TvSearchActivity : BaseFragmentActivity(), TvCheckFirstItem {
    private var isFirstItem = false
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_search)

        tv_sidebar_collapsed_search_icon.setColorFilter(ContextCompat.getColor(this, R.color.accent_color))

        googleSignIn(tv_sidebar_signin)
        googleSignOut(tv_sidebar_signout)
        googleProfileDetails(tv_sidebar_profile_photo, tv_sidebar_profile_username)

        tv_sidebar_search.setOnClickListener {
            tv_sidebar.setGone()
        }

        tv_sidebar_home.setOnClickListener {
            val intent = Intent(this, TvActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            tv_sidebar.setGone()
        }

        tv_sidebar_movies.setOnClickListener {
            val intent = Intent(this, TvCategoriesActivity::class.java)
            intent.putExtra("type", AppConstants.TV_CATEGORY_NEW_MOVIES)
            this.startActivity(intent)
            tv_sidebar.setGone()
        }

        tv_sidebar_genres.setOnClickListener {
            val intent = Intent(this, TvSingleGenreActivity::class.java)
            this.startActivity(intent)
            tv_sidebar.setGone()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (isFirstItem) {
                    tv_sidebar.setVisible()
                    val currentUser = auth.currentUser
                    if (currentUser == null) {
                        updateGoogleUI(null)
                    } else {
                        updateGoogleUI(currentUser)
                    }
                    tv_sidebar_search.requestFocus()
                }
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (tv_sidebar.isVisible) {
                    tv_sidebar.setGone()
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        if (tv_sidebar.isVisible) {
            tv_sidebar.setGone()
        } else {
            tv_sidebar.setVisible()
            val currentUser = auth.currentUser
            if (currentUser == null) {
                updateGoogleUI(null)
            } else {
                updateGoogleUI(currentUser)
            }
            tv_sidebar_search.requestFocus()
        }

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun isFirstItem(boolean: Boolean) {
        isFirstItem = boolean
    }
}