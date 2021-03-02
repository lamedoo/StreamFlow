package com.lukakordzaia.streamflow.ui.tv.categories

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentActivity
import com.lukakordzaia.streamflow.utils.setGone
import kotlinx.android.synthetic.main.tv_sidebar.*
import kotlinx.android.synthetic.main.tv_sidebar_collapsed.*

class TvCategoriesActivity : BaseFragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_categories)

        setSidebarClickListeners(
                tv_sidebar_search,
                tv_sidebar_home,
                tv_sidebar_favorites,
                tv_sidebar_movies,
                tv_sidebar_genres,
                tv_sidebar_settings
        )

        setCurrentButton(tv_sidebar_movies)

        tv_sidebar_movies.setOnClickListener {
            tv_sidebar.setGone()
        }

        tv_sidebar_collapsed_movies_icon.setColorFilter(ContextCompat.getColor(this, R.color.accent_color))

        googleSignIn(tv_sidebar_signin)
        googleSignOut(tv_sidebar_signout)
        googleProfileDetails(tv_sidebar_profile_photo, tv_sidebar_profile_username)
    }
}