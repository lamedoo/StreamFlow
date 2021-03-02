package com.lukakordzaia.streamflow.ui.tv.settings

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.lukakordzaia.streamflow.R

class TvSettingsActivity: FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_settings)
//
//        setSidebarClickListeners(
//                tv_sidebar_search,
//                tv_sidebar_home,
//                tv_sidebar_favorites,
//                tv_sidebar_movies,
//                tv_sidebar_genres,
//                tv_sidebar_settings
//        )
//
//        setCurrentButton(tv_sidebar_home)
//
//        tv_sidebar_settings.setOnClickListener {
//            tv_sidebar.setGone()
//        }
//
//        tv_sidebar_collapsed_settings_icon.setColorFilter(ContextCompat.getColor(this, R.color.accent_color))
//
//        googleSignIn(tv_sidebar_signin)
//        googleSignOut(tv_sidebar_signout)
//        googleProfileDetails(tv_sidebar_profile_photo, tv_sidebar_profile_username)
    }
}