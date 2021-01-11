package com.lukakordzaia.imoviesapp.ui.tv.main

import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.network.datamodels.TitleList
import com.squareup.picasso.Picasso

class TvCardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
//        val textView = TextView(parent.context).apply {
//            isFocusable = true
//            isFocusableInTouchMode = true
//            background = parent.resources.getDrawable(R.drawable.main_background)
//        }
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
        val movie = item as TitleList.Data
        val cardView = viewHolder.view as ImageCardView

        cardView.titleText = movie.primaryName
        cardView.contentText = movie.secondaryName
        cardView.setMainImageDimensions(313, 176)
        Picasso.get().load(movie.covers?.data?.x510).into(cardView.mainImageView)

    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}