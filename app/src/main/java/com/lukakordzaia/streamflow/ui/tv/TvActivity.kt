package com.lukakordzaia.streamflow.ui.tv

import android.os.Bundle
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.DbTitleData
import com.lukakordzaia.streamflow.interfaces.TvCheckTitleSelected
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentActivity
import com.lukakordzaia.streamflow.ui.tv.details.titledetails.TvDetailsViewModel
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tv_sidebar.*
import kotlinx.android.synthetic.main.tv_sidebar_collapsed.*
import kotlinx.android.synthetic.main.tv_top_title_header.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

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

        tvDetailsViewModel.getSingleTitleResponse.observe(this, {
            home_top_name.text = it.secondaryName
            if (it.covers?.data?.x1050!!.isNotBlank()) {
                Picasso.get().load(it.covers.data.x1050).error(R.drawable.movie_image_placeholder_landscape).into(home_top_poster)
            }
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

    override fun getTitleId(titleId: Int, continueWatchingDetails: DbTitleData?) {
        tvDetailsViewModel.getSingleTitleData(titleId)

        if (continueWatchingDetails != null) {
            home_top_watched_seekbar.setVisible()
            home_top_watched_season.setVisible()
            if (continueWatchingDetails.isTvShow) {
                home_top_watched_season.text = String.format("ს${continueWatchingDetails.season} ე${continueWatchingDetails.episode} / %02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(continueWatchingDetails.watchedDuration),
                        TimeUnit.MILLISECONDS.toSeconds(continueWatchingDetails.watchedDuration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(continueWatchingDetails.watchedDuration))
                )
            } else {
                home_top_watched_season.text = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(continueWatchingDetails.watchedDuration),
                        TimeUnit.MILLISECONDS.toMinutes(continueWatchingDetails.watchedDuration) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(continueWatchingDetails.watchedDuration)),
                        TimeUnit.MILLISECONDS.toSeconds(continueWatchingDetails.watchedDuration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(continueWatchingDetails.watchedDuration))
                )
            }

            home_top_watched_seekbar.max = continueWatchingDetails.titleDuration.toInt()
            home_top_watched_seekbar.progress = continueWatchingDetails.watchedDuration.toInt()
        } else {
            home_top_watched_season.setGone()
            home_top_watched_seekbar.setGone()
        }

    }
}