package com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitlefiles.presenters

import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.widget.Presenter
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.customviews.TvCastCardView
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleCastResponse

class TvCastPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvCastCardView(parent.context, null).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            background = ResourcesCompat.getDrawable(parent.resources, R.drawable.tv_cast_card_view_background, null)
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