package com.lukakordzaia.imoviesapp.ui.tv.main

import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.network.datamodels.TitleList
import com.lukakordzaia.imoviesapp.network.datamodels.WatchedTitleData
import com.squareup.picasso.Picasso

class WatchedCardPresenter : Presenter() {
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

        cardView.titleText = movie.primaryName
        cardView.contentText = movie.originalName
        cardView.setMainImageDimensions(313, 176)
        Picasso.get().load(movie.cover).into(cardView.mainImageView)

    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}