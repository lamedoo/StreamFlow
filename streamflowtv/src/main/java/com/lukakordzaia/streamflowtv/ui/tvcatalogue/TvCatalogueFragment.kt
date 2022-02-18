package com.lukakordzaia.streamflowtv.ui.tvcatalogue

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.leanback.widget.*
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.toTvInfoModel
import com.lukakordzaia.streamflowtv.baseclasses.BaseVerticalGridSupportFragment
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.TvSingleTitleActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class TvCatalogueFragment : BaseVerticalGridSupportFragment<TvCatalogueViewModel>() {
    private var type = 0

    override val viewModel by viewModel<TvCatalogueViewModel>()
    override val reload: () -> Unit = { viewModel.fetchContent(type, page) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        type = activity?.intent?.getSerializableExtra(AppConstants.CATALOGUE_TYPE) as Int

        viewModel.fetchContent(type, page)
        
        fragmentObservers()
        setupEventListeners(ItemViewClickedListener(), ItemViewSelectedListener())
    }

    private fun fragmentObservers() {
        viewModel.tvCatalogueList.observe(viewLifecycleOwner, {
            it.forEach { title ->
                gridAdapter.add(title)
            }
        })

        viewModel.generalLoader.observe(viewLifecycleOwner, {
            (activity as TvCatalogueActivity).setProgressBar(it == LoadingState.LOADING)
        })
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder?,
            item: Any?,
            rowViewHolder: RowPresenter.ViewHolder?,
            row: Row?
        ) {
            if (item is SingleTitleModel) {
                val intent = Intent(context, TvSingleTitleActivity::class.java).apply {
                    putExtra(AppConstants.TITLE_ID, item.id)
                    putExtra(AppConstants.IS_TV_SHOW, item.isTvShow)
                }
                activity?.startActivity(intent)
            }
        }
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
            val indexOfRow = gridAdapter.size()
            val indexOfItem = gridAdapter.indexOf(item)

            if (item is SingleTitleModel) {
                onTitleSelected?.getTitleId(item.toTvInfoModel())
            }

            val gridSize = Array(gridAdapter.size()) { i -> (i * 1) + 1 }.toList()

            onFirstItem?.isFirstItem(false, null, null)

            if (indexOfItem == 0) {
                onFirstItem?.isFirstItem(true, null, null)
            }

            gridSize.forEach {
                if (it % 6 == 0) {
                    if (indexOfItem == it) {
                        onFirstItem?.isFirstItem(true, null, null)
                    }
                }
            }

            if (indexOfItem != - 10 && indexOfRow - 10 <= indexOfItem) {
                page++
                when (activity?.intent?.getSerializableExtra(AppConstants.CATALOGUE_TYPE) as Int) {
                    AppConstants.LIST_NEW_MOVIES -> {
                        viewModel.getNewMoviesTv(page)
                    }
                    AppConstants.LIST_TOP_MOVIES -> {
                        viewModel.getTopMoviesTv(page)
                    }
                    AppConstants.LIST_TOP_TV_SHOWS -> {
                        viewModel.getTopTvShowsTv(page)
                    }
                }
            }
        }
    }
}