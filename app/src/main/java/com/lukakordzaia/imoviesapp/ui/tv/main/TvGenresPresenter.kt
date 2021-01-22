package com.lukakordzaia.imoviesapp.ui.tv.main

import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.Presenter
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.network.datamodels.GenreList

class TvGenresPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TextView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            setPadding(10,15,10,15)
            background = parent.resources.getDrawable(R.drawable.main_background)
        }

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val genre = item as GenreList.Data
        val view = viewHolder.view as TextView

        view.text = genre.primaryName
        view.setPadding(10,15,10,15)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }

}