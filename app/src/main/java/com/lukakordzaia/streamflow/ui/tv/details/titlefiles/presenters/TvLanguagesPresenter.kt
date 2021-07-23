package com.lukakordzaia.streamflow.ui.tv.details.titlefiles.presenters

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.customviews.TvSeasonsCardView
import kotlinx.android.synthetic.main.tv_details_season_item.view.*

class TvLanguagesPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvSeasonsCardView(parent.context, null)

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        cardView.background = parent.resources.getDrawable(R.drawable.rv_tv_default_card_view_background)

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val language = item as String
        val cardView = viewHolder.view as TvSeasonsCardView

        cardView.rv_tv_files_season_number.text = language

        cardView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                cardView.rv_tv_files_season_number.text = "$language - აირჩიეთ"
            } else {
                cardView.rv_tv_files_season_number.text = language
            }
        }


    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}