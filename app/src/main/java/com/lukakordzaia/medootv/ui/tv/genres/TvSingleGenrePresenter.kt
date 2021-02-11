package com.lukakordzaia.medootv.ui.tv.genres

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.lukakordzaia.medootv.R
import com.lukakordzaia.medootv.datamodels.TitleList
import com.lukakordzaia.medootv.ui.customviews.TvGenresCardView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tv_genres_card_view.view.*

class TvSingleGenrePresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvGenresCardView(parent.context, null)

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        cardView.background = parent.resources.getDrawable(R.drawable.rv_tv_default_card_view_background)

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val movie = item as TitleList.Data
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

        cardView.tv_genres_card_istvshow.text = "სერიალი"

        if (item.isTvShow == true) {
            cardView.tv_genres_card_istvshow.text = "სერიალი"
        } else {
            cardView.tv_genres_card_istvshow.text = "ფილმი"
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}