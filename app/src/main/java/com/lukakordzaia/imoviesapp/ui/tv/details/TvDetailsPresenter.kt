package com.lukakordzaia.imoviesapp.ui.tv.details

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.network.datamodels.TitleData
import com.squareup.picasso.Picasso

class TvDetailsPresenter : AbstractDetailsDescriptionPresenter() {
    override fun onBindDescription(viewHolder: ViewHolder, itemData: Any) {
        val details = itemData as TitleData.Data

        val seasons = details.seasons?.data?.size

        viewHolder.apply {
            title.text = "${details.secondaryName} / ${details.primaryName}"

            if (details.isTvShow != true) {
                subtitle.text = "${details.year}   IMDB: ${details.rating?.imdb?.score}   ${details.duration} წთ.   "
            } else {
                subtitle.text = "${details.year}   IMDB: ${details.rating?.imdb?.score}   ${seasons} სეზონი   "
            }

            body.text = details.plot?.data?.description
        }
    }
}