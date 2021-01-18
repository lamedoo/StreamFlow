package com.lukakordzaia.imoviesapp.ui.tv.main

import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.lukakordzaia.imoviesapp.network.datamodels.GenreList

class TvGenresPresenter : Presenter() {
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
        val genre = item as GenreList.Data
        val cardView = viewHolder.view as ImageCardView

        cardView.setMainImageDimensions(313, 100)
        cardView.titleText = genre.primaryName

    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}