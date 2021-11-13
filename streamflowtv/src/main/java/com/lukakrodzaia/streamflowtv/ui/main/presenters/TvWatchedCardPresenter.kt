package com.lukakrodzaia.streamflowtv.ui.main.presenters

import android.view.ViewGroup
import androidx.leanback.widget.HorizontalGridView
import androidx.leanback.widget.Presenter
import com.lukakordzaia.core.datamodels.ContinueWatchingModel
import com.lukakrodzaia.streamflowtv.R
import com.lukakrodzaia.streamflowtv.customviews.TvWatchedCardView

class TvWatchedCardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvWatchedCardView(parent.context, null).apply {
            isFocusable = true
            isFocusableInTouchMode = true
        }

        val horizontalGridView: HorizontalGridView = parent.findViewById(R.id.row_content)
        horizontalGridView.setItemSpacing(1)

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val dbTitle = item as ContinueWatchingModel
        val cardView = viewHolder.view as TvWatchedCardView

        cardView.setPoster(dbTitle.cover)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {}

}