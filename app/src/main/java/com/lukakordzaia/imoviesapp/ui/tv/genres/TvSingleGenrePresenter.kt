package com.lukakordzaia.imoviesapp.ui.tv.genres

import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.datamodels.TitleList
import com.squareup.picasso.Picasso

class TvSingleGenrePresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = ImageCardView(parent.context)
        
        cardView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                cardView.findViewById<TextView>(R.id.title_text).maxLines = 3
            } else {
                cardView.findViewById<TextView>(R.id.title_text).maxLines = 1
            }
        }

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val movie = item as TitleList.Data
        val cardView = viewHolder.view as ImageCardView

        if (!movie.primaryName.isNullOrBlank()) {
            cardView.titleText = movie.primaryName
        } else {
            cardView.titleText = movie.secondaryName
        }
        cardView.setMainImageDimensions(250, 330)

        if (movie.posters != null) {
            if (movie.posters.data != null) {
                if (!movie.posters.data.x240.isNullOrEmpty()) {
                    Picasso.get().load(movie.posters.data.x240).into(cardView.mainImageView)
                } else {
                    Picasso.get().load(R.drawable.movie_image_placeholder).into(cardView.mainImageView)
                }
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}