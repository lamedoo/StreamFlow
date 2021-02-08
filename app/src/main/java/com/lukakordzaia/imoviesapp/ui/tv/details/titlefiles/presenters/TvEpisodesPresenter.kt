package com.lukakordzaia.imoviesapp.ui.tv.details.titlefiles.presenters

import android.content.Context
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.datamodels.TitleEpisodes
import com.lukakordzaia.imoviesapp.ui.customviews.TvEpisodesCardView
import com.lukakordzaia.imoviesapp.utils.setGone
import com.lukakordzaia.imoviesapp.utils.setVisible
import kotlinx.android.synthetic.main.tv_details_episodes_item.view.*

class TvEpisodesPresenter(private val context: Context) : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvEpisodesCardView(parent.context, null)

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        cardView.background = parent.resources.getDrawable(R.drawable.rv_tv_default_card_view_background)
        cardView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                cardView.rv_tv_files_episode_poster_dim.setVisible()
                cardView.rv_tv_files_episode_name.setVisible()
            } else {
                cardView.rv_tv_files_episode_poster_dim.setGone()
                cardView.rv_tv_files_episode_name.setGone()
            }
        }

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val movie = item as TitleEpisodes
        val cardView = viewHolder.view as TvEpisodesCardView

        cardView.rv_tv_files_episode_number.text = "ეპიზოდი ${movie.episodeNum}"
        cardView.rv_tv_files_episode_name.text = movie.episodeName

        cardView.rv_tv_files_episode_container.setOnFocusChangeListener { _, _ ->  }

        Glide.with(context).load(movie.episodePoster).error(R.drawable.movie_image_placeholder_landscape).into(cardView.rv_tv_files_episode_poster)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}