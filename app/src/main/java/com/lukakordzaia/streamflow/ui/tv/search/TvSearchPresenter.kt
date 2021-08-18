package com.lukakordzaia.streamflow.ui.tv.search

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.lukakordzaia.streamflow.customviews.TvDefaultWithTitleCardView
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.utils.setImage
import kotlinx.android.synthetic.main.tv_default_card_view.view.*

class TvSearchPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvDefaultWithTitleCardView(parent.context, null)

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val movie = item as SingleTitleModel
        val cardView = viewHolder.view as TvDefaultWithTitleCardView

        cardView.tv_default_card_poster.setImage(movie.poster, true)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}