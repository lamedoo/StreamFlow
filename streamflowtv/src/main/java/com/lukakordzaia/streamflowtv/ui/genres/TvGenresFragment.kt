package com.lukakordzaia.streamflowtv.ui.genres

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.leanback.widget.*
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.datamodels.SingleTitleModel
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.toTvInfoModel
import com.lukakordzaia.streamflowtv.R
import com.lukakordzaia.streamflowtv.baseclasses.BaseBrowseSupportFragment
import com.lukakordzaia.streamflowtv.interfaces.TvCheckFirstItem
import com.lukakordzaia.streamflowtv.interfaces.TvTitleSelected
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.TvSingleTitleActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class TvGenresFragment : BaseBrowseSupportFragment<SingleGenreViewModel>() {
    override val viewModel by viewModel<SingleGenreViewModel>()
    override val reload: () -> Unit = { viewModel.fetchContentTv() }

    var onTitleSelected: TvTitleSelected? = null
    var onFirstItem: TvCheckFirstItem? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onTitleSelected = context as? TvTitleSelected
        onFirstItem = context as? TvCheckFirstItem
    }

    override fun onDetach() {
        super.onDetach()
        onTitleSelected = null
        onFirstItem = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchContentTv()

        fragmentObservers()

        setupEventListeners(ItemViewClickedListener(), ItemViewSelectedListener())
    }

    private fun fragmentObservers() {
        viewModel.singleGenreAnimation.observe(viewLifecycleOwner) {
            firstCategoryAdapter(it)
        }

        viewModel.singleGenreComedy.observe(viewLifecycleOwner) {
            secondCategoryAdapter(it)
        }

        viewModel.singleGenreMelodrama.observe(viewLifecycleOwner) {
            thirdCategoryAdapter(it)
        }

        viewModel.singleGenreHorror.observe(viewLifecycleOwner) {
            fourthCategoryAdapter(it)
        }

        viewModel.singleGenreAdventure.observe(viewLifecycleOwner) {
            fifthCategoryAdapter(it)
        }

        viewModel.singleGenreAction.observe(viewLifecycleOwner) {
            sixthCategoryAdapter(it)
        }

        viewModel.generalLoader.observe(viewLifecycleOwner) {
            (activity as TvGenresActivity).setProgressBar(it == LoadingState.LOADING)
        }
    }

    private fun firstCategoryAdapter(category: List<SingleTitleModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvSingleGenrePresenter()).apply {
            category.forEach {
                add(it)
            }
        }

        HeaderItem(getString(R.string.animation)). also {
            rowsAdapter.add(ListRow(it, listRowAdapter))
        }
    }

    private fun secondCategoryAdapter(category: List<SingleTitleModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvSingleGenrePresenter()).apply {
            category.forEach {
                add(it)
            }
        }

        HeaderItem(getString(R.string.comedy)).also { header ->
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }
    }

    private fun thirdCategoryAdapter(category: List<SingleTitleModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvSingleGenrePresenter()).apply {
            category.forEach {
                add(it)
            }
        }

        HeaderItem(getString(R.string.melodrama)).also { header ->
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }
    }

    private fun fourthCategoryAdapter(category: List<SingleTitleModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvSingleGenrePresenter()).apply {
            category.forEach {
                add(it)
            }
        }

        HeaderItem(getString(R.string.horror)).also { header ->
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }
    }

    private fun fifthCategoryAdapter(category: List<SingleTitleModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvSingleGenrePresenter()).apply {
            category.forEach {
                add(it)
            }
        }

        HeaderItem(getString(R.string.adventure)).also { header ->
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }
    }

    private fun sixthCategoryAdapter(category: List<SingleTitleModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvSingleGenrePresenter()).apply {
            category.forEach {
                add(it)
            }
        }

        HeaderItem(5, getString(R.string.action)).also { header ->
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
                itemViewHolder: Presenter.ViewHolder,
                item: Any,
                rowViewHolder: RowPresenter.ViewHolder,
                row: Row
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
            if (item is SingleTitleModel) {
                onTitleSelected?.getTitleId(item.toTvInfoModel())
            }

            val indexOfItem = ((row as ListRow).adapter as ArrayObjectAdapter).indexOf(item)

            if (indexOfItem == 0) {
                onFirstItem?.isFirstItem(true, rowsSupportFragment, rowsSupportFragment.selectedPosition)
            } else {
                onFirstItem?.isFirstItem(false, null, null)
            }
        }
    }
}