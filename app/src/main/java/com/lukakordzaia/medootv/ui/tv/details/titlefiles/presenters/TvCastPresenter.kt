package com.lukakordzaia.medootv.ui.tv.details.titlefiles.presenters

import android.content.Context
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.lukakordzaia.medootv.R
import com.lukakordzaia.medootv.datamodels.TitleCast
import com.lukakordzaia.medootv.ui.customviews.TvCastCardView
import kotlinx.android.synthetic.main.tv_cast_card_item.view.*

class TvCastPresenter(private val context: Context) : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvCastCardView(parent.context, null)

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        cardView.background = parent.resources.getDrawable(R.drawable.tv_cast_card_view_background)

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val cast = item as TitleCast.Data
        val cardView = viewHolder.view as TvCastCardView

        cardView.cast_item_name.text = cast.originalName

        cardView.cast_item_root.setOnFocusChangeListener { _, _ ->  }

        Glide.with(context).load(cast.poster).error(R.drawable.movie_image_placeholder_landscape).into(cardView.cast_item_poster)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}