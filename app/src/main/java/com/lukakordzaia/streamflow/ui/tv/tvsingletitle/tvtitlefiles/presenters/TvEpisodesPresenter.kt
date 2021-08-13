package com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitlefiles.presenters

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.core.view.updatePadding
import androidx.leanback.widget.Presenter
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.customviews.TvEpisodesCardView
import com.lukakordzaia.streamflow.datamodels.TitleEpisodes
import com.lukakordzaia.streamflow.utils.setGone
import com.lukakordzaia.streamflow.utils.setImage
import com.lukakordzaia.streamflow.utils.setVisible
import com.lukakordzaia.streamflow.utils.setVisibleOrGone
import kotlinx.android.synthetic.main.tv_details_episodes_item.view.*

class TvEpisodesPresenter(private val context: Context, private val currentEpisode: Int?, private val isSeason: Boolean) : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvEpisodesCardView(parent.context, null)

        cardView.isFocusable = true
        cardView.background = ResourcesCompat.getDrawable(context.resources, R.drawable.rv_tv_default_card_view_background, null)
        cardView.isFocusableInTouchMode = true

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val episode = item as TitleEpisodes
        val cardView = viewHolder.view as TvEpisodesCardView

        if (isSeason) {
            cardView.rv_tv_files_chosen_container.setVisibleOrGone(episode.episodeNum == currentEpisode)
        }

        cardView.rv_tv_files_episode_number.text = "ეპიზოდი ${episode.episodeNum}"
        cardView.rv_tv_files_episode_name.text = episode.episodeName
        cardView.rv_tv_files_episode_poster.setImage(episode.episodePoster, false)

        cardView.setOnFocusChangeListener { _, hasFocus ->
            cardView.rv_tv_files_episode_poster_dim.setVisibleOrGone(hasFocus)
            cardView.rv_tv_files_episode_name.setVisibleOrGone(hasFocus)

            if (episode.episodeNum == currentEpisode) {
                cardView.rv_tv_files_chosen_container.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        context.resources,
                        if (hasFocus) R.drawable.phone_chosen_episode_indicator_secondary else R.drawable.phone_chosen_episode_indicator,
                        null
                    )
                )
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}