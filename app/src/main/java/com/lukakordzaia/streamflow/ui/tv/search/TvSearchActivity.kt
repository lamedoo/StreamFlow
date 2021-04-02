package com.lukakordzaia.streamflow.ui.tv.search

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentActivity
import com.lukakordzaia.streamflow.ui.customviews.SearchEditText
import com.lukakordzaia.streamflow.utils.hideKeyboard
import com.lukakordzaia.streamflow.utils.setGone
import kotlinx.android.synthetic.main.activity_tv_search.*
import kotlinx.android.synthetic.main.tv_sidebar.*
import kotlinx.android.synthetic.main.tv_sidebar_collapsed.*

class TvSearchActivity : BaseFragmentActivity() {
    private lateinit var fragment: TvSearchFragmentNew

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_search)

        fragment = supportFragmentManager.findFragmentById(R.id.tv_search_fragment) as TvSearchFragmentNew

        setSidebarClickListeners(
                tv_sidebar_search,
                tv_sidebar_home,
                tv_sidebar_favorites,
                tv_sidebar_movies,
                tv_sidebar_genres,
                tv_sidebar_settings
        )

        setCurrentButton(tv_sidebar_search)

        tv_sidebar_search.setOnClickListener {
            tv_sidebar.setGone()
        }

        tv_sidebar_collapsed_search_icon.setColorFilter(
                ContextCompat.getColor(
                        this,
                        R.color.accent_color
                )
        )

        googleSignIn(tv_sidebar_signin)
        googleSignOut(tv_sidebar_signout)
        googleProfileDetails(tv_sidebar_profile_photo, tv_sidebar_profile_username)

        tv_search_title_text.setQueryTextChangeListener(object : SearchEditText.QueryTextListener {
            override fun onQueryTextSubmit(query: String?) {
                fragment.clearRowsAdapter()
                if (!query.isNullOrBlank()) {
                    fragment.setSearchQuery(query)
                }
                tv_search_title_text.hideKeyboard()
            }


            override fun onQueryTextChange(newText: String?) {
            }
        })
    }
}