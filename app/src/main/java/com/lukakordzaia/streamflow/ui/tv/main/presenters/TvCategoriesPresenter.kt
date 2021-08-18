package com.lukakordzaia.streamflow.ui.tv.main.presenters

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.lukakordzaia.streamflow.customviews.TvCategoriesCardView
import com.lukakordzaia.streamflow.datamodels.TvCategoriesList

class TvCategoriesPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvCategoriesCardView(parent.context, null)

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val categories = item as TvCategoriesList
        val cardView = viewHolder.view as TvCategoriesCardView

        cardView.setTitle(categories.categoriesTitle)
        cardView.setPoster(categories.categoriesIcon)

    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }
}