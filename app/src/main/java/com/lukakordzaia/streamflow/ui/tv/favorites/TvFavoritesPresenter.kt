package com.lukakordzaia.streamflow.ui.tv.favorites

import android.content.Context
import android.view.ViewGroup
import androidx.leanback.widget.HorizontalGridView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.customviews.TvDefaultCardView
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleResponse
import com.lukakordzaia.streamflow.utils.setImage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tv_default_card_view.view.*

class TvFavoritesPresenter(private val context: Context) : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvDefaultCardView(parent.context, null)

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true

        val horizontalGridView: HorizontalGridView = parent.findViewById(R.id.row_content)
        horizontalGridView.setItemSpacing(1)

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val movie = item as SingleTitleModel
        val cardView = viewHolder.view as TvDefaultCardView

        cardView.tv_default_card_name.text = movie.displayName

        cardView.tv_default_card_poster.setImage(movie.poster, true)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}