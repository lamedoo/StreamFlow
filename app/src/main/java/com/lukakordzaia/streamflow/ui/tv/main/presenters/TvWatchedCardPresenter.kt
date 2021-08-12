package com.lukakordzaia.streamflow.ui.tv.main.presenters

import android.view.ViewGroup
import androidx.leanback.widget.HorizontalGridView
import androidx.leanback.widget.Presenter
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.customviews.TvWatchedCardView
import com.lukakordzaia.streamflow.datamodels.ContinueWatchingModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tv_watched_card_view.view.*

class TvWatchedCardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = object : TvWatchedCardView(parent.context, null) {
        }

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true

        val horizontalGridView: HorizontalGridView = parent.findViewById(R.id.row_content)
        horizontalGridView.setItemSpacing(1)

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val dbTitle = item as ContinueWatchingModel
        val cardView = viewHolder.view as TvWatchedCardView

        Picasso.get().load(dbTitle.cover).into(cardView.tv_watched_card_poster)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}