package com.lukakordzaia.streamflow.ui.tv.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.leanback.widget.*
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.ContinueWatchingModel
import com.lukakordzaia.streamflow.datamodels.NewSeriesModel
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.interfaces.TvCheckFirstItem
import com.lukakordzaia.streamflow.interfaces.TvCheckTitleSelected
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.sharedpreferences.SharedPreferences
import com.lukakordzaia.streamflow.ui.baseclasses.fragments.BaseBrowseSupportFragment
import com.lukakordzaia.streamflow.ui.phone.home.HomeViewModel
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvMainPresenter
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvNewSeriesPresenter
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvWatchedCardPresenter
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.TvSingleTitleActivity
import com.lukakordzaia.streamflow.utils.AppConstants
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvMainFragment : BaseBrowseSupportFragment<HomeViewModel>() {
    private val sharedPreferences: SharedPreferences by inject()
    override val viewModel by viewModel<HomeViewModel>()
    override val reload: () -> Unit = { viewModel.fetchContent(1) }

    private var hasContinueWatching = false
    private var hasUserSuggestion = sharedPreferences.getLoginToken() != ""

    var onTitleSelected: TvCheckTitleSelected? = null
    var onFirstItem: TvCheckFirstItem? = null

    private lateinit var watchListAdapter: ListRow
    private lateinit var newMoviesAdapter: ListRow
    private lateinit var topMoviesAdapter: ListRow
    private lateinit var suggestionsAdapter: ListRow
    private lateinit var topTvShowsAdapter: ListRow
    private lateinit var newSeriesAdapter: ListRow


    override fun onAttach(context: Context) {
        super.onAttach(context)
        onTitleSelected = context as? TvCheckTitleSelected
        onFirstItem = context as? TvCheckFirstItem
    }

    override fun onDetach() {
        super.onDetach()
        onTitleSelected = null
        onFirstItem = null
    }

    override fun onStart() {
        super.onStart()
        if (sharedPreferences.getTvVideoPlayerOn()) {
            viewModel.checkAuthDatabase()
            sharedPreferences.saveTvVideoPlayerOn(false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.checkAuthDatabase()

        fragmentObservers()

        setupEventListeners(ItemViewClickedListener(), ItemViewSelectedListener())
    }

    private fun fragmentObservers() {
        //is needed if user is not signed in and titles are saved in room
        viewModel.contWatchingData.observe(viewLifecycleOwner, {
            viewModel.getContinueWatchingTitlesFromApi(it)
        })

        viewModel.continueWatchingList.observe(viewLifecycleOwner, {
            watchedListRowsAdapter(it)
        })

        viewModel.newMovieList.observe(viewLifecycleOwner, {
            newMoviesRowsAdapter(it)
        })

        viewModel.topMovieList.observe(viewLifecycleOwner, {
            topMoviesRowsAdapter(it)
        })

        viewModel.userSuggestionsList.observe(viewLifecycleOwner, {
            userSuggestionsRowsAdapter(it)
        })

        viewModel.topTvShowList.observe(viewLifecycleOwner, {
            topTvShowsRowsAdapter(it)
        })

        viewModel.newSeriesList.observe(viewLifecycleOwner, {
            newSeriesRowsAdapter(it)
        })

        viewModel.generalLoader.observe(viewLifecycleOwner, {
            (activity as TvActivity).setProgressBar(it == LoadingState.LOADING)
            if (it == LoadingState.LOADED) {
                setRowsAdapter()
            }
        })
    }

    private fun watchedListRowsAdapter(items: List<ContinueWatchingModel>) {
        hasContinueWatching = !items.isNullOrEmpty()

        val listRowAdapter = ArrayObjectAdapter(TvWatchedCardPresenter()).apply {
            addAll(0, items)
        }

        watchListAdapter = ListRow(HeaderItem(0, getString(R.string.continue_watching)), listRowAdapter)
    }

    private fun newMoviesRowsAdapter(items: List<SingleTitleModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvMainPresenter()).apply {
            addAll(0, items)
        }

        newMoviesAdapter = ListRow(HeaderItem(0, getString(R.string.new_movies)), listRowAdapter)
    }

    private fun topMoviesRowsAdapter(items: List<SingleTitleModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvMainPresenter()).apply {
            addAll(0, items)
        }

        topMoviesAdapter = ListRow(HeaderItem(0, getString(R.string.top_movies)), listRowAdapter)
    }

    private fun userSuggestionsRowsAdapter(items: List<SingleTitleModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvMainPresenter()).apply {
            addAll(0, items)
        }

        suggestionsAdapter = ListRow(HeaderItem(0, getString(R.string.we_suggest_watching)), listRowAdapter)
    }

    private fun topTvShowsRowsAdapter(items: List<SingleTitleModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvMainPresenter()).apply {
            addAll(0, items)
        }

        topTvShowsAdapter = ListRow(HeaderItem(0, getString(R.string.top_tv_shows)), listRowAdapter)
    }

    private fun newSeriesRowsAdapter(items: List<NewSeriesModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvNewSeriesPresenter()).apply {
            addAll(0, items)
        }

        newSeriesAdapter = ListRow(HeaderItem(0, getString(R.string.new_series)), listRowAdapter)
    }

    private fun setRowsAdapter() {
        val rows = when {
            hasContinueWatching && hasUserSuggestion -> listOf(watchListAdapter, newMoviesAdapter, topMoviesAdapter, suggestionsAdapter, topTvShowsAdapter, newSeriesAdapter)
            hasContinueWatching && !hasUserSuggestion -> listOf(watchListAdapter, newMoviesAdapter, topMoviesAdapter, topTvShowsAdapter, newSeriesAdapter)
            !hasContinueWatching && !hasUserSuggestion -> listOf(newMoviesAdapter, topMoviesAdapter, topTvShowsAdapter, newSeriesAdapter)
            else -> listOf(newMoviesAdapter, topMoviesAdapter, topTvShowsAdapter, newSeriesAdapter)
        }

        rowsAdapter.addAll(0, rows)
    }
    
    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
                itemViewHolder: Presenter.ViewHolder,
                item: Any,
                rowViewHolder: RowPresenter.ViewHolder,
                row: Row
        ) {
            when (item) {
                is SingleTitleModel -> {
                    val intent = Intent(context, TvSingleTitleActivity::class.java).apply {
                        putExtra(AppConstants.TITLE_ID, item.id)
                        putExtra(AppConstants.IS_TV_SHOW, item.isTvShow)
                    }
                    requireActivity().startActivity(intent)
                }
                is NewSeriesModel -> {
                    val intent = Intent(context, TvSingleTitleActivity::class.java).apply {
                        putExtra(AppConstants.TITLE_ID, item.id)
                        putExtra(AppConstants.IS_TV_SHOW, item.isTvShow)
                    }
                    requireActivity().startActivity(intent)
                }
                is ContinueWatchingModel -> {
                    val intent = Intent(context, TvSingleTitleActivity::class.java).apply {
                        putExtra(AppConstants.TITLE_ID, item.id)
                        putExtra(AppConstants.IS_TV_SHOW, item.isTvShow)
                        putExtra(AppConstants.CONTINUE_WATCHING_NOW, true)
                    }
                    requireActivity().startActivity(intent)
                }
            }
        }
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
            val indexOfItem = ((row as ListRow).adapter as ArrayObjectAdapter).indexOf(item)

            when (item) {
                is SingleTitleModel -> onTitleSelected?.getTitleId(item.id, null)
                is NewSeriesModel -> onTitleSelected?.getTitleId(item.id, null)
                is ContinueWatchingModel -> onTitleSelected?.getTitleId(item.id, item)
            }

            if (indexOfItem == 0) {
                onFirstItem?.isFirstItem(true, rowsSupportFragment, rowsSupportFragment.selectedPosition)
            } else {
                onFirstItem?.isFirstItem(false, null, null)
            }
        }
    }
}