package com.lukakordzaia.streamflow.utils

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.ContinueWatchingModel
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.titles.GetTitlesResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.user.GetContinueWatchingResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.user.GetUserWatchlistResponse

inline fun <T : Fragment> T.applyBundle(block: Bundle.() -> Unit): T {
    val bundle = Bundle()
    bundle.block()
    arguments = bundle
    return this
}

fun <T : Fragment> T.navController(navId: NavDirections) {
    findNavController().navigate(navId)
}

fun Context?.createToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    this?.let { Toast.makeText(it, message, duration).show() }
}

fun View.setVisible() {
    visibility = View.VISIBLE
}

fun View.setGone() {
    visibility = View.GONE
}

fun View.setInvisible() {
    visibility = View.INVISIBLE
}

fun View.setMargin(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
    val params = (layoutParams as? ViewGroup.MarginLayoutParams)
    params?.setMargins(
        left ?: params.leftMargin,
        top ?: params.topMargin,
        right ?: params.rightMargin,
        bottom ?: params.bottomMargin)
    layoutParams = params
}

fun View.setVisibleOrGone(visibility: Boolean) {
    this.visibility = if (visibility) View.VISIBLE else View.GONE
}

fun View.showKeyboard() {
    this.requestFocus()
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyboard() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun List<GetTitlesResponse.Data>.toTitleListModel(): List<SingleTitleModel> {
    return map {
        SingleTitleModel(
            id = it.id,
            isTvShow = it.isTvShow?: false,
            displayName = if (it.primaryName.isNotEmpty()) it.primaryName else it.secondaryName,
            nameGeo = it.primaryName,
            nameEng = it.secondaryName,
            poster = it.posters?.data?.x240,
            cover = it.covers?.data?.x1050,
            description = null,
            imdbId = null,
            imdbScore = null,
            releaseYear = it.year.toString(),
            duration = null,
            seasonNum = null,
            country = null,
            trailer = if (it.trailers?.data?.isNotEmpty() == true) it.trailers.data[0]?.fileUrl else null,
            watchlist = it.userWantsToWatch?.data?.status,
            titleDuration = it.userWatch?.data?.duration,
            watchedDuration = it.userWatch?.data?.progress,
            currentSeason = it.userWatch?.data?.season,
            currentEpisode = it.userWatch?.data?.episode,
            currentLanguage = it.userWatch?.data?.language,
            visibility = it.userWatch?.data?.visible
        )
    }
}

fun GetSingleTitleResponse.toSingleTitleModel(): SingleTitleModel {
    val title = this.data

    return SingleTitleModel(
        id = title.id,
        isTvShow = title.isTvShow,
        displayName = if (title.primaryName.isNotEmpty()) title.primaryName else title.secondaryName,
        nameGeo = if (title.primaryName.isNotEmpty()) title.primaryName else "N/A",
        nameEng = if (title.secondaryName.isNotEmpty()) title.secondaryName else "N/A",
        poster = title.posters.data?.x240,
        cover = title.covers?.data?.x1050,
        description = if (title.plot.data.description.isNotEmpty()) title.plot.data.description else "აღწერა არ მოიძებნა",
        imdbId = title.imdbUrl.substring(27, title.imdbUrl.length),
        imdbScore = if (title.rating.imdb != null) title.rating.imdb.score.toString() else "N/A",
        releaseYear = title.year.toString(),
        duration = "${title.duration} წ.",
        seasonNum = if (title.seasons != null) {
            if (title.seasons.data.isNotEmpty()) title.seasons.data.size else 0
        } else {
            0
        },
        country = if (title.countries.data.isNotEmpty()) title.countries.data[0].secondaryName else "N/A",
        trailer = if (title.trailers.data.isNotEmpty()) title.trailers.data[0].fileUrl else null,
        watchlist = title.userWantsToWatch?.data?.status,
        titleDuration = title.userWatch?.data?.duration,
        watchedDuration = title.userWatch?.data?.progress,
        currentSeason = title.userWatch?.data?.season,
        currentEpisode = title.userWatch?.data?.episode,
        currentLanguage = title.userWatch?.data?.language,
        visibility = title.userWatch?.data?.visible
    )
}

fun List<GetUserWatchlistResponse.Data>.toWatchListModel(): List<SingleTitleModel> {
    return map { data ->
        val it = data.movie.data

        SingleTitleModel(
            id = it.id,
            isTvShow = it.isTvShow?: false,
            displayName = if (it.primaryName.isNotEmpty()) it.primaryName else it.secondaryName,
            nameGeo = it.primaryName,
            nameEng = it.secondaryName,
            poster = it.posters?.data?.x240,
            cover = it.covers?.data?.x1050,
            description = null,
            imdbId = null,
            imdbScore = null,
            releaseYear = it.year.toString(),
            duration = null,
            seasonNum = null,
            country = null,
            trailer = if (it.trailers?.data?.isNotEmpty() == true) it.trailers.data[0]?.fileUrl else null,
            watchlist = it.userWantsToWatch?.data?.status,
            titleDuration = it.userWatch?.data?.duration,
            watchedDuration = it.userWatch?.data?.progress,
            currentSeason = it.userWatch?.data?.season,
            currentEpisode = it.userWatch?.data?.episode,
            currentLanguage = it.userWatch?.data?.language,
            visibility = it.userWatch?.data?.visible
        )
    }
}

fun List<GetContinueWatchingResponse.Data>.toContinueWatchingModel(): List<ContinueWatchingModel> {
    return map {
        val movie = it.movie.data

        ContinueWatchingModel(
            cover = movie.posters?.data?.x240,
            duration = movie.duration,
            id = movie.id,
            isTvShow = movie.isTvShow,
            primaryName = if (movie.primaryName.isNotEmpty()) movie.primaryName else movie.secondaryName,
            originalName = if (movie.primaryName.isNotEmpty()) movie.primaryName else movie.secondaryName,
            watchedDuration = it.progress,
            titleDuration = it.duration,
            season = it.season,
            episode = it.episode,
            language = it.language
        )
    }
}

fun ImageView.setImage(image: String?, isPhone: Boolean) {
    Glide.with(context)
        .load(image ?: if (isPhone) R.drawable.placeholder_movie else R.drawable.placeholder_movie_landscape)
        .placeholder(if (isPhone) R.drawable.placeholder_movie else R.drawable.placeholder_movie_landscape)
        .into(this)
}

fun View.setDrawableBackground(background: Int) {
    this.background = ResourcesCompat.getDrawable(resources, background, null)
}

fun TextView.setColor(color: Int) {
    this.setTextColor(ResourcesCompat.getColor(resources, color, null))
}