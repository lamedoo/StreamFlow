package com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvepisodes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.domainmodels.VideoPlayerData
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.utils.setVisibleOrGone
import com.lukakordzaia.streamflowtv.interfaces.TvCheckFirstItem
import com.lukakordzaia.streamflowtv.interfaces.TvTitleSelected
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.TvSingleTitleActivity
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitlerelated.presenters.TvSeasonsPresenter
import org.koin.androidx.viewmodel.ext.android.viewModel


class TvSeasonBrowse : VerticalGridSupportFragment() {
    var titleId: Int = 0

    val viewModel by viewModel<TvEpisodesViewModel>()
    private lateinit var gridAdapter: ArrayObjectAdapter

    private var onTitleSelected: TvTitleSelected? = null
    private var onFirstItem: TvCheckFirstItem? = null

    private var firstSelection = true
    private var continueSeason = 1
    private var continueEpisode = 1

    private var currentSeason = 0

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
        val args = arguments
        continueSeason = args?.getInt("season") ?: 1
        continueEpisode = args?.getInt("episode") ?: 1

        val titleId = activity?.intent?.getSerializableExtra(AppConstants.TITLE_ID) as? Int
        val videoPlayerData = activity?.intent?.getParcelableExtra(AppConstants.VIDEO_PLAYER_DATA) as? VideoPlayerData

        this.titleId = titleId ?: videoPlayerData!!.titleId

        viewModel.getNumOfSeasons(this.titleId)
        fragmentObservers()
        setupEventListeners(ItemViewClickedListener(), ItemViewSelectedListener())

        initGridAdapter()
    }

    private fun fragmentObservers() {
        viewModel.generalLoader.observe(viewLifecycleOwner) {
            (parentFragment as TvEpisodesFragment).changeLoadingState(it)
        }

        viewModel.numOfSeasons.observe(viewLifecycleOwner) {
            val seasonCount = Array(it!!) { i -> (i * 1) + 1 }.toList()

            gridAdapter.clear()
            gridAdapter.addAll(0, seasonCount)

            setSelectedPosition(continueSeason - 1)

            if (firstSelection) {
                (parentFragment as TvEpisodesFragment).setFragmentFocus("season")
                firstSelection = false
            }
        }
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
                if (currentSeason != item) {
                    (parentFragment as TvEpisodesFragment).setEpisodesFragment(item, continueEpisode)
                    currentSeason = item
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        onTitleSelected = null
        onFirstItem = null
    }
}