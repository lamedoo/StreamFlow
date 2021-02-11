package com.lukakordzaia.streamflow.ui.tv.main.presenters

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.lukakordzaia.streamflow.datamodels.DbTitleData
import com.lukakordzaia.streamflow.ui.customviews.TvWatchedCardView
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
        val dbTitle = item as DbTitleData
        val cardView = viewHolder.view as TvWatchedCardView
        cardView.tv_watched_card_season.text = String.format("ს${dbTitle.season} ე${dbTitle.episode} / %02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(dbTitle.watchedDuration),
                TimeUnit.MILLISECONDS.toSeconds(dbTitle.watchedDuration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(dbTitle.watchedDuration))
        )

        if (dbTitle.isTvShow) {
            cardView.tv_watched_card_season.text = String.format("ს${dbTitle.season} ე${dbTitle.episode} / %02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(dbTitle.watchedDuration),
                    TimeUnit.MILLISECONDS.toSeconds(dbTitle.watchedDuration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(dbTitle.watchedDuration))
            )
        } else {
            cardView.tv_watched_card_season.text = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(dbTitle.watchedDuration),
                    TimeUnit.MILLISECONDS.toSeconds(dbTitle.watchedDuration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(dbTitle.watchedDuration))
            )
        }

        Picasso.get().load(dbTitle.cover).into(cardView.tv_watched_card_poster)

        cardView.tv_watched_card_seekbar.max = dbTitle.titleDuration.toInt()
        cardView.tv_watched_card_seekbar.progress = dbTitle.watchedDuration.toInt()
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}