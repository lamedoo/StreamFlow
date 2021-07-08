package com.lukakordzaia.streamflow.ui.tv.genres

import android.view.ViewGroup
import androidx.leanback.widget.HorizontalGridView
import androidx.leanback.widget.Presenter
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.network.models.response.titles.GetTitlesResponse
import com.lukakordzaia.streamflow.ui.customviews.TvGenresCardView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tv_genres_card_view.view.*

class TvSingleGenrePresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvGenresCardView(parent.context, null)

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true

        val horizontalGridView: HorizontalGridView = parent.findViewById(R.id.row_content)
        horizontalGridView.setItemSpacing(1)

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val movie = item as GetTitlesResponse.Data
        val cardView = viewHolder.view as TvGenresCardView

        if (!movie.primaryName.isNullOrBlank()) {
            cardView.tv_genres_card_name.text = movie.primaryName
        } else {
            cardView.tv_genres_card_name.text = movie.secondaryName
        }

        if (movie.posters != null) {
            if (movie.posters.data != null) {
                if (!movie.posters.data.x240.isNullOrEmpty()) {
                    Picasso.get().load(movie.posters.data.x240).into(cardView.tv_genres_card_poster)
                } else {
                    Picasso.get().load(R.drawable.movie_image_placeholder).into(cardView.tv_genres_card_poster)
                }
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}