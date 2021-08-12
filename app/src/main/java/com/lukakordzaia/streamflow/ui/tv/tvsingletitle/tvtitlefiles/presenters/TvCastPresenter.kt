package com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitlefiles.presenters

import android.content.Context
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.customviews.TvCastCardView
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleCastResponse
import com.lukakordzaia.streamflow.utils.setImage
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
        val cast = item as GetSingleTitleCastResponse.Data
        val cardView = viewHolder.view as TvCastCardView

        cardView.cast_item_name.text = cast.originalName

        cardView.cast_item_root.setOnFocusChangeListener { _, _ ->  }

        cardView.cast_item_poster.setImage(cast.poster, false)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}