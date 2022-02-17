package com.lukakordzaia.streamflowtv.ui.genres

import android.view.ViewGroup
import androidx.leanback.widget.HorizontalGridView
import androidx.leanback.widget.Presenter
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.streamflowtv.R
import com.lukakordzaia.streamflowtv.customviews.TvDefaultCardView

class TvSingleGenrePresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvDefaultCardView(parent.context, null)

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true

        val horizontalGridView: HorizontalGridView = parent.findViewById(R.id.row_content)
        horizontalGridView.setItemSpacing(1)

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val movie = item as SingleTitleModel
        val cardView = viewHolder.view as TvDefaultCardView

        cardView.setPoster(movie.poster)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}