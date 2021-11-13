package com.lukakrodzaia.streamflowtv.ui.tvsingletitle.tvtitlefiles.presenters

import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.widget.Presenter
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleCastResponse
import com.lukakrodzaia.streamflowtv.R
import com.lukakrodzaia.streamflowtv.customviews.TvCastCardView

class TvCastPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvCastCardView(parent.context, null).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            background = ResourcesCompat.getDrawable(parent.resources, R.drawable.background_cast_card_tv, null)
        }

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val cast = item as GetSingleTitleCastResponse.Data
        val cardView = viewHolder.view as TvCastCardView

        cardView.setTitle(cast.originalName)
        cardView.setPoster(cast.poster)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {}

}