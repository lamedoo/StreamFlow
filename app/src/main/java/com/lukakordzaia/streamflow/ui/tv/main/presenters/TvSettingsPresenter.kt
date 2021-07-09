package com.lukakordzaia.streamflow.ui.tv.main.presenters

import android.content.Context
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.customviews.TvCategoriesCardView
import com.lukakordzaia.streamflow.datamodels.TvSettingsList
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tv_categories_card_view.view.*

class TvSettingsPresenter(private val context: Context) : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvCategoriesCardView(parent.context, null)

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val settings = item as TvSettingsList
        val cardView = viewHolder.view as TvCategoriesCardView

        cardView.tv_categories_card_title.text = settings.settingsTitle
        Picasso.get().load(R.drawable.clear_db_icon).error(R.drawable.clear_db_icon).into(cardView.tv_categories_card_poster)

    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}