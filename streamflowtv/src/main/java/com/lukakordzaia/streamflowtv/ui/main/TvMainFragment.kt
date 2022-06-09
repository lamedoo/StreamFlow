package com.lukakordzaia.streamflowtv.ui.main

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.leanback.widget.*
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.domain.domainmodels.ContinueWatchingModel
import com.lukakordzaia.core.domain.domainmodels.GithubReleaseModel
import com.lukakordzaia.core.domain.domainmodels.NewSeriesModel
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.toTvInfoModel
import com.lukakordzaia.core.sharedpreferences.SharedPreferences
import com.lukakordzaia.core.utils.DialogUtils
import com.lukakordzaia.core.utils.EventObserver
import com.lukakordzaia.core.utils.startPackageActivity
import com.lukakordzaia.streamflowtv.R
import com.lukakordzaia.streamflowtv.baseclasses.BaseBrowseSupportFragment
import com.lukakordzaia.streamflowtv.interfaces.TvCheckFirstItem
import com.lukakordzaia.streamflowtv.interfaces.TvTitleSelected
import com.lukakordzaia.streamflowtv.ui.main.presenters.TvMainPresenter
import com.lukakordzaia.streamflowtv.ui.main.presenters.TvNewSeriesPresenter
import com.lukakordzaia.streamflowtv.ui.main.presenters.TvWatchedCardPresenter
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.TvSingleTitleActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class TvMainFragment : BaseBrowseSupportFragment<TvMainViewModel>() {
    private val sharedPreferences: SharedPreferences by inject()
    override val viewModel by viewModel<TvMainViewModel>()
    override val reload: () -> Unit = { viewModel.fetchContent(1) }

    private var hasContinueWatching = false
    private var hasUserSuggestion = sharedPreferences.getLoginToken() != ""

    var onTitleSelected: TvTitleSelected? = null
    var onFirstItem: TvCheckFirstItem? = null

    private var watchListAdapter: ListRow? = null
    private var newMoviesAdapter: ListRow? = null
    private var topMoviesAdapter: ListRow? = null
    private var suggestionsAdapter: ListRow? = null
    private var topTvShowsAdapter: ListRow? = null
    private var newSeriesAdapter: ListRow? = null

    private lateinit var downloadReleaseDialog: Dialog

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

    override fun onStart() {
        super.onStart()
        if (sharedPreferences.getRefreshContinueWatching()) {
            viewModel.checkAuthDatabase()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.checkAuthDatabase()

        viewModel.checkGithubReleases(getString(R.string.version_number, requireContext().packageManager.getPackageInfo(requireContext().packageName, 0).versionName))
        fragmentObservers()

        setupEventListeners(ItemViewClickedListener(), ItemViewSelectedListener())
    }

    private fun fragmentObservers() {
        viewModel.continueWatchingList.observe(viewLifecycleOwner) {
            watchedListRowsAdapter(it)

            if (!it.isNullOrEmpty() && sharedPreferences.getRefreshContinueWatching()) {
                sharedPreferences.saveRefreshContinueWatching(false)
            }
        }

        viewModel.newMovieList.observe(viewLifecycleOwner) {
            newMoviesRowsAdapter(it)
        }

        viewModel.topMovieList.observe(viewLifecycleOwner) {
            topMoviesRowsAdapter(it)
        }

        viewModel.userSuggestionsList.observe(viewLifecycleOwner) {
            userSuggestionsRowsAdapter(it)
        }

        viewModel.topTvShowList.observe(viewLifecycleOwner) {
            topTvShowsRowsAdapter(it)
        }

        viewModel.newSeriesList.observe(viewLifecycleOwner) {
            newSeriesRowsAdapter(it)
        }

        viewModel.generalLoader.observe(viewLifecycleOwner) {
            (activity as TvActivity).setProgressBar(it == LoadingState.LOADING)
            if (it == LoadingState.LOADED) {
                setRowsAdapter()
            }
        }

        viewModel.releaseUrl.observe(viewLifecycleOwner, EventObserver {
            downloadReleaseDialog(it)
        })
    }

    private fun watchedListRowsAdapter(items: List<ContinueWatchingModel>) {
        hasContinueWatching = items.isNotEmpty()

        val listRowAdapter = ArrayObjectAdapter(TvWatchedCardPresenter()).apply {
            addAll(0, items)
        }

        watchListAdapter = ListRow(HeaderItem(0, getString(R.string.continue_watching)), listRowAdapter)

        setRowsAdapter()
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
        val row: MutableList<ListRow> = ArrayList()
        rowsAdapter.clear()


        when {
            hasContinueWatching && hasUserSuggestion -> {
                watchListAdapter?.let { row.add(it) }
                newMoviesAdapter?.let { row.add(it) }
                topMoviesAdapter?.let { row.add(it) }
                suggestionsAdapter?.let { row.add(it) }
                topTvShowsAdapter?.let { row.add(it) }
                newSeriesAdapter?.let { row.add(it) }
            }
            hasContinueWatching && !hasUserSuggestion -> {
                watchListAdapter?.let { row.add(it) }
                newMoviesAdapter?.let { row.add(it) }
                topMoviesAdapter?.let { row.add(it) }
                topTvShowsAdapter?.let { row.add(it) }
                newSeriesAdapter?.let { row.add(it) }
            }
            !hasContinueWatching && hasUserSuggestion -> {
                newMoviesAdapter?.let { row.add(it) }
                topMoviesAdapter?.let { row.add(it) }
                suggestionsAdapter?.let { row.add(it) }
                topTvShowsAdapter?.let { row.add(it) }
                newSeriesAdapter?.let { row.add(it) }
            }
            !hasContinueWatching && !hasUserSuggestion -> {
                newMoviesAdapter?.let { row.add(it) }
                topMoviesAdapter?.let { row.add(it) }
                topTvShowsAdapter?.let { row.add(it) }
                newSeriesAdapter?.let { row.add(it) }
            }
            else -> {
                newMoviesAdapter?.let { row.add(it) }
                topMoviesAdapter?.let { row.add(it) }
                topTvShowsAdapter?.let { row.add(it) }
                newSeriesAdapter?.let { row.add(it) }
            }
        }

        rowsAdapter.addAll(0, row)
    }

    private fun downloadReleaseDialog(release: GithubReleaseModel) {
        downloadReleaseDialog = DialogUtils.downloadReleaseAlertDialog(requireContext()) {
            viewModel.downloadNewRelease(release.downloadUrl!!, "streamflow-${release.tag}.apk", requireContext()) { uri ->
                uri?.let {
                    downloadReleaseDialog.dismiss()
                    startPackageActivity(this, File(it)) {
                        viewModel.newToastMessage(getString(R.string.no_activity_found))
                    }
                }
            }
        }
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
                is SingleTitleModel -> onTitleSelected?.getTitleId(item.toTvInfoModel())
                is NewSeriesModel -> onTitleSelected?.getTitleId(item.toTvInfoModel())
                is ContinueWatchingModel -> onTitleSelected?.getTitleId(item.toTvInfoModel(), item)
            }

            if (indexOfItem == 0) {
                onFirstItem?.isFirstItem(true, rowsSupportFragment, rowsSupportFragment.selectedPosition)
            } else {
                onFirstItem?.isFirstItem(false, null, null)
            }
        }
    }
}