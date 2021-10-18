package com.lukakordzaia.streamflow.ui.tv.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.leanback.widget.*
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.ContinueWatchingModel
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.datamodels.TvCategoriesList
import com.lukakordzaia.streamflow.interfaces.TvCheckFirstItem
import com.lukakordzaia.streamflow.interfaces.TvCheckTitleSelected
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.sharedpreferences.SharedPreferences
import com.lukakordzaia.streamflow.ui.baseclasses.fragments.BaseBrowseSupportFragment
import com.lukakordzaia.streamflow.ui.phone.home.HomeViewModel
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvCategoriesPresenter
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvMainPresenter
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvWatchedCardPresenter
import com.lukakordzaia.streamflow.ui.tv.tvcatalogue.TvCatalogueActivity
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.TvSingleTitleActivity
import com.lukakordzaia.streamflow.utils.AppConstants
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvMainFragment : BaseBrowseSupportFragment<HomeViewModel>() {
    override val viewModel by viewModel<HomeViewModel>()
    private val sharedPreferences: SharedPreferences by inject()

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
        initRowsAdapter()

        watchedListRowsAdapter()
        newMoviesRowsAdapter()
        topMoviesRowsAdapter()
        topTvShowsRowsAdapter()
        categoriesRowsAdapter()

        setupEventListeners(ItemViewClickedListener(), ItemViewSelectedListener())
    }

    private fun initRowsAdapter() {
        val firstHeaderItem = ListRow(HeaderItem(0, AppConstants.TV_CONTINUE_WATCHING), ArrayObjectAdapter(TvMainPresenter()))
        val secondHeaderItem = ListRow(HeaderItem(1, AppConstants.TV_TOP_MOVIES), ArrayObjectAdapter(TvMainPresenter()))
        val thirdHeaderItem = ListRow(HeaderItem(2, AppConstants.TV_TOP_TV_SHOWS), ArrayObjectAdapter(TvMainPresenter()))
        val fourthHeaderItem = ListRow(HeaderItem(3, AppConstants.TV_GENRES), ArrayObjectAdapter(TvMainPresenter()))
        val sixthHeaderItem = ListRow(HeaderItem(5, AppConstants.TV_NEW_MOVIES), ArrayObjectAdapter(TvMainPresenter()))
        val initListRows = mutableListOf(firstHeaderItem, sixthHeaderItem, secondHeaderItem, thirdHeaderItem, fourthHeaderItem)
        rowsAdapter.addAll(0, initListRows)
    }

    private fun watchedListRowsAdapter() {
        viewModel.checkAuthDatabase()
        viewModel.contWatchingData.observe(viewLifecycleOwner, {
            viewModel.getContinueWatchingTitlesFromApi(it)
        })

        viewModel.continueWatchingList.observe(viewLifecycleOwner, { continueWatching ->
            setSelectedPosition(if (continueWatching.isNullOrEmpty()) 1 else 0, continueWatching.isNullOrEmpty())

            viewModel.continueWatchingLoader.observe(viewLifecycleOwner, {
                when (it) {
                    LoadingState.LOADING -> {}
                    LoadingState.LOADED -> {
                        val listRowAdapter = ArrayObjectAdapter(TvWatchedCardPresenter()).apply {
                            continueWatching.forEach { titles ->
                                add(titles)
                            }
                        }

                        HeaderItem(0, AppConstants.TV_CONTINUE_WATCHING).also {
                            rowsAdapter.replace(0, ListRow(it, listRowAdapter))
                        }
                    }
                }
            })
        })
    }

    private fun newMoviesRowsAdapter() {
        viewModel.newMovieList.observe(viewLifecycleOwner, {
            val listRowAdapter = ArrayObjectAdapter(TvMainPresenter()).apply {
                addAll(0, it)
            }

            HeaderItem(1, AppConstants.TV_NEW_MOVIES).also { header ->
                rowsAdapter.replace(1, ListRow(header, listRowAdapter))
            }
        })
    }

    private fun topMoviesRowsAdapter() {
        viewModel.topMovieList.observe(viewLifecycleOwner, {
            val listRowAdapter = ArrayObjectAdapter(TvMainPresenter()).apply {
                addAll(0, it)
            }

            HeaderItem(2, AppConstants.TV_TOP_MOVIES).also { header ->
                rowsAdapter.replace(2, ListRow(header, listRowAdapter))
            }
        })
    }

    private fun topTvShowsRowsAdapter() {
        viewModel.topTvShowList.observe(viewLifecycleOwner, {
            val listRowAdapter = ArrayObjectAdapter(TvMainPresenter()).apply {
                addAll(0, it)
            }

            HeaderItem(3, AppConstants.TV_TOP_TV_SHOWS).also { header ->
                rowsAdapter.replace(3, ListRow(header, listRowAdapter))
            }
        })
    }

    private fun categoriesRowsAdapter() {
        val listRowAdapter = ArrayObjectAdapter(TvCategoriesPresenter()).apply {
            add(TvCategoriesList(0, "ტოპ ფილები", R.drawable.icon_star))
            add(TvCategoriesList(1, "ტოპ სერიალები", R.drawable.icon_star))
        }

        HeaderItem(4, AppConstants.TV_GENRES).also { header ->
            rowsAdapter.replace(4, ListRow(header, listRowAdapter))
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

            if (item is SingleTitleModel) {
                onTitleSelected?.getTitleId(item.id, null)
            } else if (item is ContinueWatchingModel) {
                onTitleSelected?.getTitleId(item.id, item)
            }

            if (indexOfItem == 0) {
                onFirstItem?.isFirstItem(true, rowsSupportFragment, rowsSupportFragment.selectedPosition)
            } else {
                onFirstItem?.isFirstItem(false, null, null)
            }
        }
    }
}