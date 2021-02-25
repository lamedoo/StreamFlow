package com.lukakordzaia.streamflow.ui.tv.search

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentActivity
import com.lukakordzaia.streamflow.ui.tv.categories.TvCategoriesActivity
import com.lukakordzaia.streamflow.ui.tv.genres.TvSingleGenreActivity
import com.lukakordzaia.streamflow.utils.AppConstants
import com.lukakordzaia.streamflow.utils.setGone
import kotlinx.android.synthetic.main.tv_sidebar.*
import kotlinx.android.synthetic.main.tv_sidebar_collapsed.*

class TvSearchActivity : BaseFragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_search)

        tv_sidebar_collapsed_search_icon.setColorFilter(ContextCompat.getColor(this, R.color.accent_color))

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
}