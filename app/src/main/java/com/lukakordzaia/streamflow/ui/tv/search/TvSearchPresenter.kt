package com.lukakordzaia.streamflow.ui.tv.search

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.network.models.imovies.response.titles.GetTitlesResponse
import com.lukakordzaia.streamflow.ui.customviews.TvDefaultWithTitleCardView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tv_default_card_view.view.*

class TvSearchPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvDefaultWithTitleCardView(parent.context, null)

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val movie = item as GetTitlesResponse.Data
        val cardView = viewHolder.view as TvDefaultWithTitleCardView

        if (!movie.primaryName.isNullOrBlank()) {
            cardView.tv_default_card_name.text = movie.primaryName
        } else {
            cardView.tv_default_card_name.text = movie.secondaryName
        }

        if (movie.posters != null) {
            if (movie.posters.data != null) {
                if (!movie.posters.data.x240.isNullOrEmpty()) {
                    Picasso.get().load(movie.posters.data.x240).into(cardView.tv_default_card_poster)
                } else {
                    Picasso.get().load(R.drawable.movie_image_placeholder).into(cardView.tv_default_card_poster)
                }
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}