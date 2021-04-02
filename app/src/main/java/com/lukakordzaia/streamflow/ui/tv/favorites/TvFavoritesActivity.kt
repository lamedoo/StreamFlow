package com.lukakordzaia.streamflow.ui.tv.favorites

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import androidx.core.content.ContextCompat
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.DbTitleData
import com.lukakordzaia.streamflow.helpers.TvCheckTitleSelected
import com.lukakordzaia.streamflow.helpers.TvHasFavoritesListener
import com.lukakordzaia.streamflow.ui.baseclasses.BaseFragmentActivity
import com.lukakordzaia.streamflow.ui.tv.details.titledetails.TvDetailsViewModel
import com.lukakordzaia.streamflow.utils.createToast
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setVisible
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_tv_favorites.*
import kotlinx.android.synthetic.main.tv_sidebar.*
import kotlinx.android.synthetic.main.tv_sidebar_collapsed.*
import kotlinx.android.synthetic.main.tv_top_title_header.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvFavoritesActivity: BaseFragmentActivity(), TvCheckTitleSelected, TvHasFavoritesListener {
    private val tvDetailsViewModel: TvDetailsViewModel by viewModel()

    private var hasFavorites = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_favorites)

        setSidebarClickListeners(
                tv_sidebar_search,
                tv_sidebar_home,
                tv_sidebar_favorites,
                tv_sidebar_movies,
                tv_sidebar_genres,
                tv_sidebar_settings
        )

        setCurrentButton(tv_sidebar_favorites)

        tv_sidebar_favorites.setOnClickListener {
            tv_sidebar.setGone()
        }

        tv_sidebar_collapsed_favorites_icon.setColorFilter(ContextCompat.getColor(this, R.color.accent_color))

        googleSignIn(tv_sidebar_signin)
        googleSignOut(tv_sidebar_signout)
        googleProfileDetails(tv_sidebar_profile_photo, tv_sidebar_profile_username)

        Handler(Looper.getMainLooper()).postDelayed(Runnable {

            if (!hasFavorites) {
                no_favorites_container.setVisible()
                this.createToast("სამწუხაროდ, არ გაქვთ ფავორიტები არჩეული")
            }
        }, 2500)


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

    override fun getTitleId(titleId: Int, continueWatchingDetails: DbTitleData?) {
        tvDetailsViewModel.getSingleTitleData(titleId)
    }

    override fun hasFavorites(has: Boolean) {
        Log.d("hasfavorites", has.toString())
        hasFavorites = has
    }
}