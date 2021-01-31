package com.lukakordzaia.imoviesapp.ui.tv.main

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
import com.lukakordzaia.imoviesapp.ui.phone.MainActivity
import com.squareup.picasso.Picasso

class TvSettingsPresenter(private val context: Context) : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = ImageCardView(parent.context)

        cardView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                cardView.findViewById<TextView>(R.id.title_text).maxLines = 3
            } else {
                cardView.findViewById<TextView>(R.id.title_text).maxLines = 1
            }
        }

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val settings = item as TvSettingsList
        val cardView = viewHolder.view as ImageCardView

        cardView.titleText = settings.settingsTitle
        cardView.setMainImageDimensions(300, 300)
        cardView.mainImage = ResourcesCompat.getDrawable(context.resources, R.drawable.delete_forever_icon, null)

    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}