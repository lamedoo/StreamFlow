package com.lukakordzaia.streamflow.ui.tv

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.helpers.TvCheckFirstItem
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentActivity
import com.lukakordzaia.streamflow.ui.tv.categories.TvCategoriesActivity
import com.lukakordzaia.streamflow.ui.tv.genres.TvSingleGenreActivity
import com.lukakordzaia.streamflow.ui.tv.search.TvSearchActivity
import com.lukakordzaia.streamflow.utils.AppConstants
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import kotlinx.android.synthetic.main.tv_sidebar.*
import kotlinx.android.synthetic.main.tv_sidebar_collapsed.*

class TvActivity : BaseFragmentActivity(), TvCheckFirstItem {
    private var doubleBackToExitPressedOnce = false
    private var isFirstItem = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv)

        tv_sidebar_collapsed_home_icon.setColorFilter(ContextCompat.getColor(this, R.color.accent_color))

        googleSignIn(tv_sidebar_signin)
        googleSignOut(tv_sidebar_signout)
        googleProfileDetails(tv_sidebar_profile_photo, tv_sidebar_profile_username)

        tv_sidebar_search.setOnClickListener {
            startActivity(Intent(this, TvSearchActivity::class.java))
            tv_sidebar.setGone()
        }

        tv_sidebar_home.setOnClickListener {
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
                    tv_sidebar_home.requestFocus()
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
            tv_sidebar_home.requestFocus()
        }

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "გასავლელად, კიდევ ერთხელ დააჭირეთ", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun isFirstItem(boolean: Boolean) {
        isFirstItem = boolean
    }
}