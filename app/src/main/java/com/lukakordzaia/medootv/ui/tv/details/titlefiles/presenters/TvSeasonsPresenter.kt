package com.lukakordzaia.medootv.ui.tv.details.titlefiles.presenters

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.lukakordzaia.medootv.R
import com.lukakordzaia.medootv.ui.customviews.TvSeasonsCardView
import kotlinx.android.synthetic.main.tv_details_season_item.view.*

class TvSeasonsPresenter() : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvSeasonsCardView(parent.context, null)

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        cardView.background = parent.resources.getDrawable(R.drawable.rv_tv_default_card_view_background)

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val movie = item as Int
        val cardView = viewHolder.view as TvSeasonsCardView

        cardView.rv_tv_files_season_number.text = "სეზონი $movie"
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}