package com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitlefiles.presenters

import android.content.Context
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.widget.Presenter
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.customviews.TvEpisodesCardView
import com.lukakordzaia.streamflow.datamodels.TitleEpisodes

class TvEpisodesPresenter(private val context: Context, private val currentEpisode: Int?, private val isSeason: Boolean) : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvEpisodesCardView(parent.context, null)

        cardView.isFocusable = true
        cardView.background = ResourcesCompat.getDrawable(context.resources, R.drawable.background_episodes_card_tv, null)
        cardView.isFocusableInTouchMode = true

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val episode = item as TitleEpisodes
        val cardView = viewHolder.view as TvEpisodesCardView

        if (isSeason) {
            cardView.currentIndicatorVisibility(episode.episodeNum == currentEpisode)
        }

        cardView.setNumber("ეპიზოდი ${episode.episodeNum}")
        cardView.setName(episode.episodeName)
        cardView.setPoster(episode.episodePoster)

        if (episode.titleDuration != 0.toLong()) {
            cardView.setSeekbarVisibility(true)
            cardView.setProgress(episode.watchDuration!!.toInt(), episode.titleDuration!!.toInt())
        }

        cardView.setOnFocusChangeListener { _, hasFocus ->
            cardView.posterDimVisibility(hasFocus)
            cardView.nameVisibility(hasFocus)

            if (episode.episodeNum == currentEpisode) {
                cardView.setIndicatorDrawable(
                    ResourcesCompat.getDrawable(
                        context.resources,
                        if (hasFocus) R.drawable.indicator_current_episode_dark else R.drawable.indicator_current_episode_light,
                        null
                    )
                )
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}