package com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvepisodes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.datamodels.SingleTitleModel
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.streamflowtv.R
import com.lukakordzaia.streamflowtv.baseclasses.BaseVerticalGridSupportFragment
import com.lukakordzaia.streamflowtv.interfaces.TvCheckFirstItem
import com.lukakordzaia.streamflowtv.interfaces.TvTitleSelected
import com.lukakordzaia.streamflowtv.ui.tvcatalogue.TvCataloguePresenter
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.TvSingleTitleActivity
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitlerelated.presenters.TvSeasonsPresenter
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.getScopeId


class TvSeasonBrowse : VerticalGridSupportFragment() {
    var titleId: Int? = 0

    val viewModel by sharedViewModel<TvEpisodesViewModel>()
    private lateinit var gridAdapter: ArrayObjectAdapter

    private var onTitleSelected: TvTitleSelected? = null
    private var onFirstItem: TvCheckFirstItem? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onTitleSelected = context as? TvTitleSelected
        onFirstItem = context as? TvCheckFirstItem
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initGridPresenter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleIdFromDetails = activity?.intent?.getSerializableExtra(AppConstants.TITLE_ID) as? Int

        titleId = titleIdFromDetails

        fragmentObservers()
        setupEventListeners(ItemViewClickedListener(), ItemViewSelectedListener())

        initGridAdapter()
    }

    private fun fragmentObservers() {
        viewModel.numOfSeasons.observe(viewLifecycleOwner, {
            val seasonCount = Array(it!!) { i -> (i * 1) + 1 }.toList()

            seasonCount.forEach { season ->
                gridAdapter.add(season)
            }
        })

        viewModel.continueWatchingDetails.observe(viewLifecycleOwner, {
            viewModel.setChosenSeason(it?.season ?: 1)
        })

        viewModel.chosenSeason.observe(viewLifecycleOwner, {
            setSelectedPosition(it - 1)
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

    private fun initGridPresenter() {
        title = ""
        val gridPresenter = StandardGridPresenter(FocusHighlight.ZOOM_FACTOR_NONE, false)
        gridPresenter.numberOfColumns = 1
        gridPresenter.shadowEnabled = false
        setGridPresenter(gridPresenter)
    }

    private fun initGridAdapter() {
        gridAdapter = ArrayObjectAdapter(TvSeasonsPresenter())
        adapter = gridAdapter
    }

    private fun setupEventListeners(click: OnItemViewClickedListener, select: OnItemViewSelectedListener) {
        onItemViewClickedListener = click
        setOnItemViewSelectedListener(select)
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
            if (item is Int) {
                viewModel.getSeasonFiles(titleId!!, item)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        onTitleSelected = null
        onFirstItem = null
    }
}