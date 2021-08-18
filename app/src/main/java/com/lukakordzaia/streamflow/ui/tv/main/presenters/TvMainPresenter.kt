package com.lukakordzaia.streamflow.ui.tv.main.presenters

import android.view.ViewGroup
import androidx.leanback.widget.HorizontalGridView
import androidx.leanback.widget.Presenter
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.customviews.TvDefaultCardView
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel

class TvMainPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvDefaultCardView(parent.context, null).apply {
            isFocusable = true
            isFocusableInTouchMode = true
        }

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