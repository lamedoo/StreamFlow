package com.lukakordzaia.streamflow.ui.tv

import android.os.Bundle
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.helpers.TvCheckTitleSelected
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentActivity
import com.lukakordzaia.streamflow.ui.tv.details.titledetails.TvDetailsViewModel
import com.lukakordzaia.streamflow.utils.setGone
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_tv.*
import kotlinx.android.synthetic.main.tv_sidebar.*
import kotlinx.android.synthetic.main.tv_sidebar.tv_sidebar
import kotlinx.android.synthetic.main.tv_sidebar_collapsed.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvActivity : BaseFragmentActivity(), TvCheckTitleSelected {
    private val tvDetailsViewModel: TvDetailsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv)

        setSidebarClickListeners(
                tv_sidebar_search,
                tv_sidebar_home,
                tv_sidebar_favorites,
                tv_sidebar_movies,
                tv_sidebar_genres,
                tv_sidebar_settings
        )

        setCurrentButton(tv_sidebar_home)

        tv_sidebar_home.setOnClickListener {
            tv_sidebar.setGone()
        }

        tv_sidebar_collapsed_home_icon.setColorFilter(ContextCompat.getColor(this, R.color.accent_color))

        googleSignIn(tv_sidebar_signin)
        googleSignOut(tv_sidebar_signout)
        googleProfileDetails(tv_sidebar_profile_photo, tv_sidebar_profile_username)

        tvDetailsViewModel.singleTitleData.observe(this, {
            home_top_name.text = it.secondaryName
            Picasso.get().load(it.covers?.data?.x1050)
                .error(R.drawable.movie_image_placeholder_landscape).into(home_top_poster)
            home_top_year.text = "${it.year}   ·"
            if (it.isTvShow) {
                home_top_duration.text = "${it.seasons?.data?.size} სეზონი   ·"
            } else {
                home_top_duration.text = "${it.duration.toString()} წთ   ·"
            }
            if (it.rating.imdb?.score != null) {
                home_top_rating.text = "IMDB ${it.rating.imdb.score.toString()}"
            }
        })

        tvDetailsViewModel.titleGenres.observe(this, {
            home_top_genres.text = TextUtils.join(", ", it)
        })
    }

    override fun getTitleId(titleId: Int) {
        tvDetailsViewModel.getSingleTitleData(titleId)
    }
}