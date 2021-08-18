package com.lukakordzaia.streamflow.ui.tv.search

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.lukakordzaia.streamflow.customviews.TvSimilarCardView
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel

class TvSimilarPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvSimilarCardView(parent.context, null)

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val movie = item as SingleTitleModel
        val cardView = viewHolder.view as TvSimilarCardView

        cardView.setPoster(movie.poster)
        cardView.setTitle(movie.displayName)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}