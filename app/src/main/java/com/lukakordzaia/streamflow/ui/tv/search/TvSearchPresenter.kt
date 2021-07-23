package com.lukakordzaia.streamflow.ui.tv.search

import android.content.Context
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.customviews.TvDefaultWithTitleCardView
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.network.models.imovies.response.titles.GetTitlesResponse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tv_default_card_view.view.*

class TvSearchPresenter(private val context: Context) : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvDefaultWithTitleCardView(parent.context, null)

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val movie = item as SingleTitleModel
        val cardView = viewHolder.view as TvDefaultWithTitleCardView

        cardView.tv_default_card_name.text = movie.displayName

        Glide.with(context)
            .load(movie.poster?: R.drawable.movie_image_placeholder)
            .placeholder(R.drawable.movie_image_placeholder_landscape)
            .into(cardView.tv_default_card_poster)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}