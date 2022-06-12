package com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitlerelated.presenters

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.lukakordzaia.core.domain.domainmodels.SeasonEpisodesModel
import com.lukakordzaia.streamflowtv.customviews.TvEpisodesCardView

class TvEpisodesPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvEpisodesCardView(parent.context, null)

        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        cardView.layoutParams = ViewGroup.LayoutParams(width, height)

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val episode = item as SeasonEpisodesModel
        val cardView = viewHolder.view as TvEpisodesCardView

        cardView.setNumber("ეპიზოდი ${episode.episode}")
        cardView.setName(episode.title)
        cardView.setPoster(episode.cover)

        cardView.setSeekbarVisibility(episode.titleDuration?.toInt() != 0)
        if (episode.titleDuration?.toInt() != 0) {
            cardView.binding.itemSeekBar.max = episode.titleDuration!!.toInt()
            cardView.binding.itemSeekBar.progress = episode.watchedDuration!!.toInt()
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {}

}