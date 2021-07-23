package com.lukakordzaia.streamflow.ui.tv.main.presenters

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.lukakordzaia.streamflow.customviews.TvCategoriesCardView
import com.lukakordzaia.streamflow.datamodels.TvCategoriesList
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tv_categories_card_view.view.*

class TvCategoriesPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = TvCategoriesCardView(parent.context, null)

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val categories = item as TvCategoriesList
        val cardView = viewHolder.view as TvCategoriesCardView

        cardView.tv_categories_card_title.text = categories.categoriesTitle
        Picasso.get().load(categories.categoriesIcon).error(categories.categoriesIcon).into(cardView.tv_categories_card_poster)

    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }
}