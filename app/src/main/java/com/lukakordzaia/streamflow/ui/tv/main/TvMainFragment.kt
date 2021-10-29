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
import com.lukakordzaia.streamflow.datamodels.TvCategoriesList
import com.lukakordzaia.streamflow.interfaces.TvCheckFirstItem
import com.lukakordzaia.streamflow.interfaces.TvCheckTitleSelected
import com.lukakordzaia.streamflow.sharedpreferences.SharedPreferences
import com.lukakordzaia.streamflow.ui.baseclasses.fragments.BaseBrowseSupportFragment
import com.lukakordzaia.streamflow.ui.phone.home.HomeViewModel
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvCategoriesPresenter
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvMainPresenter
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvNewSeriesPresenter
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvWatchedCardPresenter
import com.lukakordzaia.streamflow.ui.tv.tvcatalogue.TvCatalogueActivity
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.TvSingleTitleActivity
import com.lukakordzaia.streamflow.utils.AppConstants
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvMainFragment : BaseBrowseSupportFragment<HomeViewModel>() {
    private val sharedPreferences: SharedPreferences by inject()
    override val viewModel by viewModel<HomeViewModel>()
    override val reload: () -> Unit = { viewModel.fetchContent(1) }

    private var hasContinueWatching = false

    var onTitleSelected: TvCheckTitleSelected? = null
    var onFirstItem: TvCheckFirstItem? = null

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

        baseRowsAdapter()
        fragmentObservers()
        categoriesRowsAdapter()

        setupEventListeners(ItemViewClickedListener(), ItemViewSelectedListener())
    }

    private fun baseRowsAdapter() {
        val watchlistRow = ListRow(HeaderItem(""), ArrayObjectAdapter(TvMainPresenter()))
        val newMoviesRow = ListRow(HeaderItem(""), ArrayObjectAdapter(TvMainPresenter()))
        val topMoviesRow = ListRow(HeaderItem(""), ArrayObjectAdapter(TvMainPresenter()))
        val topTvShowsRow = ListRow(HeaderItem(""), ArrayObjectAdapter(TvMainPresenter()))
        val newSeriesRow = ListRow(HeaderItem(""), ArrayObjectAdapter(TvMainPresenter()))
        val categoriesRow = ListRow(HeaderItem(""), ArrayObjectAdapter(TvMainPresenter()))
        val initListRows = mutableListOf(watchlistRow, newMoviesRow, topMoviesRow, topTvShowsRow, newSeriesRow, categoriesRow)
        rowsAdapter.addAll(0, initListRows)
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

        viewModel.topTvShowList.observe(viewLifecycleOwner, {
            topTvShowsRowsAdapter(it)
        })

        viewModel.newSeriesList.observe(viewLifecycleOwner, {
            newSeriesRowsAdapter(it)
        })
    }

    private fun watchedListRowsAdapter(items: List<ContinueWatchingModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvWatchedCardPresenter()).apply {
            addAll(0, items)
        }

        HeaderItem(0, getString(R.string.continue_watching)).also {
            rowsAdapter.replace(0, ListRow(it, listRowAdapter))
        }

        hasContinueWatching = !items.isNullOrEmpty()
    }

    private fun newMoviesRowsAdapter(items: List<SingleTitleModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvMainPresenter()).apply {
            addAll(0, items)
        }

        HeaderItem(if (hasContinueWatching) 1 else 0, getString(R.string.new_movies)).also { header ->
            rowsAdapter.replace(if (hasContinueWatching) 1 else 0, ListRow(header, listRowAdapter))
        }
    }

    private fun topMoviesRowsAdapter(items: List<SingleTitleModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvMainPresenter()).apply {
            addAll(0, items)
        }

        HeaderItem(if (hasContinueWatching) 2 else 1, getString(R.string.top_movies)).also { header ->
            rowsAdapter.replace(if (hasContinueWatching) 2 else 1, ListRow(header, listRowAdapter))
        }
    }

    private fun topTvShowsRowsAdapter(items: List<SingleTitleModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvMainPresenter()).apply {
            addAll(0, items)
        }

        HeaderItem(if (hasContinueWatching) 3 else 2, getString(R.string.top_tv_shows)).also { header ->
            rowsAdapter.replace(if (hasContinueWatching) 3 else 2, ListRow(header, listRowAdapter))
        }
    }

    private fun newSeriesRowsAdapter(items: List<NewSeriesModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvNewSeriesPresenter()).apply {
            addAll(0, items)
        }

        HeaderItem(if (hasContinueWatching) 4 else 3, getString(R.string.new_series)).also { header ->
            rowsAdapter.replace(if (hasContinueWatching) 4 else 3, ListRow(header, listRowAdapter))
        }
    }

    private fun categoriesRowsAdapter() {
        val listRowAdapter = ArrayObjectAdapter(TvCategoriesPresenter()).apply {
            add(TvCategoriesList(0, "ტოპ ფილები", R.drawable.icon_star))
            add(TvCategoriesList(1, "ტოპ სერიალები", R.drawable.icon_star))
        }

        HeaderItem(if (hasContinueWatching) 5 else 4, getString(R.string.categories)).also { header ->
            rowsAdapter.replace(if (hasContinueWatching) 5 else 4, ListRow(header, listRowAdapter))
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
                is TvCategoriesList -> {
                    when (item.categoriesId) {
                        0 -> {
                            val intent = Intent(context, TvCatalogueActivity::class.java).apply {
                                putExtra(AppConstants.CATALOGUE_TYPE, AppConstants.LIST_TOP_MOVIES)
                            }
                            requireActivity().startActivity(intent)
                        }
                        1 -> {
                            val intent = Intent(context, TvCatalogueActivity::class.java).apply {
                                putExtra(AppConstants.CATALOGUE_TYPE, AppConstants.LIST_TOP_TV_SHOWS)
                            }
                            requireActivity().startActivity(intent)
                        }
                    }
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