package com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitlerelated.presenters

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.lukakordzaia.streamflowtv.customviews.TvSeasonsCardView

class TvSeasonsPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvSeasonsCardView(parent.context, null)

        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        cardView.layoutParams = ViewGroup.LayoutParams(width, height)

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val season = item as Int
        val cardView = viewHolder.view as TvSeasonsCardView

        cardView.setSeason("სეზონი $season")
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
}