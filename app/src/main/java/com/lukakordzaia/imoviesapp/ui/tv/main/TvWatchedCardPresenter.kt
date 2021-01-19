package com.lukakordzaia.imoviesapp.ui.tv.main

import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.lukakordzaia.imoviesapp.network.datamodels.WatchedTitleData
import com.squareup.picasso.Picasso
import java.util.concurrent.TimeUnit

class TvWatchedCardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = object : ImageCardView(parent.context) {
            override fun setSelected(selected: Boolean) {
                super.setSelected(selected)
            }
        }

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val movie = item as WatchedTitleData
        val cardView = viewHolder.view as ImageCardView

        if (movie.isTvShow) {
            cardView.titleText = "ს${movie.season} ე${movie.episode} / ${String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(movie.watchedTime),
                    TimeUnit.MILLISECONDS.toSeconds(movie.watchedTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(movie.watchedTime))
            )}"
        } else {
            cardView.titleText = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(movie.watchedTime),
                    TimeUnit.MILLISECONDS.toSeconds(movie.watchedTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(movie.watchedTime))
            )
        }

        cardView.contentText = movie.language
        cardView.setMainImageDimensions(250, 350)
        Picasso.get().load(movie.cover).into(cardView.mainImageView)

    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}