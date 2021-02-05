package com.lukakordzaia.imoviesapp.ui.tv.main.presenters

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.datamodels.GenreList
import com.lukakordzaia.imoviesapp.datamodels.TitleList
import com.lukakordzaia.imoviesapp.datamodels.TvSettingsList
import com.lukakordzaia.imoviesapp.ui.customviews.TvCategoriesCardView
import com.lukakordzaia.imoviesapp.ui.phone.MainActivity
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
        Picasso.get().load(R.drawable.tv_delete_forever_icon).error(R.drawable.tv_delete_forever_icon).into(cardView.tv_categories_card_poster)

    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}