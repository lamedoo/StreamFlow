package com.lukakrodzaia.streamflowtv.ui.tvsingletitle.tvtitlefiles.presenters

import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.widget.Presenter
import com.lukakrodzaia.streamflowtv.R
import com.lukakrodzaia.streamflowtv.customviews.TvSeasonsCardView

class TvSeasonsPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvSeasonsCardView(parent.context, null)

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        cardView.background = ResourcesCompat.getDrawable(parent.resources, R.drawable.background_episodes_card_tv, null)
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val movie = item as Int
        val cardView = viewHolder.view as TvSeasonsCardView

        cardView.setSeason("სეზონი $movie")
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}