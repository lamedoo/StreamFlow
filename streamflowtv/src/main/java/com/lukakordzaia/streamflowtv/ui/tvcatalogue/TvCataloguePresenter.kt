package com.lukakordzaia.streamflowtv.ui.tvcatalogue

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.streamflowtv.customviews.TvDefaultCardView

class TvCataloguePresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvDefaultCardView(parent.context, null)

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val movie = item as SingleTitleModel
        val cardView = viewHolder.view as TvDefaultCardView

        cardView.setPoster(movie.poster)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
}