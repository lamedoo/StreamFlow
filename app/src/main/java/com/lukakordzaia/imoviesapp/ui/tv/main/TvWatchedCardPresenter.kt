package com.lukakordzaia.imoviesapp.ui.tv.main

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.lukakordzaia.imoviesapp.datamodels.WatchedTitleData
import com.lukakordzaia.imoviesapp.ui.customviews.TvWatchedCardView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tv_watched_card_view.view.*
import java.util.concurrent.TimeUnit

class TvWatchedCardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = object : TvWatchedCardView(parent.context, null) {
        }

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val movie = item as WatchedTitleData
        val cardView = viewHolder.view as TvWatchedCardView
        cardView.tv_watched_card_season.text = "ს${movie.season} ე${movie.episode} / ${String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(movie.watchedTime),
                TimeUnit.MILLISECONDS.toSeconds(movie.watchedTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(movie.watchedTime))
        )}"

        if (movie.isTvShow) {
            cardView.tv_watched_card_season.text = "ს${movie.season} ე${movie.episode} / ${String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(movie.watchedTime),
                    TimeUnit.MILLISECONDS.toSeconds(movie.watchedTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(movie.watchedTime))
            )}"
        } else {
            cardView.tv_watched_card_season.text = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(movie.watchedTime),
                    TimeUnit.MILLISECONDS.toSeconds(movie.watchedTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(movie.watchedTime))
            )
        }

//        cardView.contentText = movie.language
        cardView.setPosterDimensions(250, 350)
        Picasso.get().load(movie.cover).into(cardView.tv_watched_card_poster)

    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}