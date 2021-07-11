package com.lukakordzaia.streamflow.ui.tv.main

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.DbTitleData
import com.lukakordzaia.streamflow.datamodels.TvCategoriesList
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.helpers.CustomListRowPresenter
import com.lukakordzaia.streamflow.interfaces.TvCheckFirstItem
import com.lukakordzaia.streamflow.interfaces.TvCheckTitleSelected
import com.lukakordzaia.streamflow.network.models.imovies.response.titles.GetTitlesResponse
import com.lukakordzaia.streamflow.ui.phone.home.HomeViewModel
import com.lukakordzaia.streamflow.ui.tv.categories.TvCategoriesActivity
import com.lukakordzaia.streamflow.ui.tv.details.TvDetailsActivity
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvCardPresenter
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvCategoriesPresenter
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvHeaderItemPresenter
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvWatchedCardPresenter
import com.lukakordzaia.streamflow.ui.tv.tvvideoplayer.TvVideoPlayerActivity
import com.lukakordzaia.streamflow.utils.AppConstants
import com.lukakordzaia.streamflow.utils.EventObserver
import com.lukakordzaia.streamflow.utils.createToast
import org.koin.androidx.viewmodel.ext.android.viewModel


class TvMainFragment : BrowseSupportFragment() {
    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var rowsAdapter: ArrayObjectAdapter
    private val page = 1
    lateinit var metrics: DisplayMetrics
    private lateinit var backgroundManager: BackgroundManager

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        val containerDock = view!!.findViewById<View>(R.id.browse_container_dock) as FrameLayout
        val params = containerDock.layoutParams as ViewGroup.MarginLayoutParams
        val resources: Resources = inflater.context.resources
        val newHeaderMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, resources.displayMetrics).toInt()
        val offsetToZero: Int = -resources.getDimensionPixelSize(R.dimen.lb_browse_rows_margin_top)
        params.topMargin = offsetToZero + newHeaderMargin
        containerDock.layoutParams = params

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            prepareEntranceTransition()
        }

        headersState = HEADERS_DISABLED


        setHeaderPresenterSelector(object : PresenterSelector() {
            override fun getPresenter(item: Any?): Presenter {
                return TvHeaderItemPresenter()
            }
        })

        val listRowPresenter = CustomListRowPresenter().apply {
            shadowEnabled = false
            selectEffectEnabled = false
        }
        rowsAdapter = ArrayObjectAdapter(listRowPresenter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRowsAdapter()

        homeViewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                requireContext().createToast(AppConstants.NO_INTERNET)
                Handler(Looper.getMainLooper()).postDelayed({
                    homeViewModel.refreshContent()
                }, 5000)
            }
        })

        if (Firebase.auth.currentUser != null) {
            homeViewModel.clearContinueWatchingTitleList()
            homeViewModel.getContinueWatchingFromFirestore()
        } else {
            homeViewModel.getContinueWatchingFromRoom(requireContext()).observe(viewLifecycleOwner, {
                homeViewModel.clearContinueWatchingTitleList()
                if (!it.isNullOrEmpty()) {
                    homeViewModel.getContinueWatchingTitlesFromApi(it)
                }
            })
        }

        Handler(Looper.getMainLooper()).postDelayed({
            homeViewModel.getTopMovies(page)
            homeViewModel.getTopTvShows(page)
            homeViewModel.getNewMovies(page)
            startEntranceTransition()
        }, 2000)

        homeViewModel.continueWatchingList.observe(viewLifecycleOwner, {
            watchedListRowsAdapter(it)

            if (it.isNullOrEmpty()) {
                setSelectedPosition(1, true)
            } else {
                setSelectedPosition(0, true)
            }
        })

        homeViewModel.topMovieList.observe(viewLifecycleOwner, {
            topMoviesRowsAdapter(it)
        })

        homeViewModel.newMovieList.observe(viewLifecycleOwner, {
            newMoviesRowsAdapter(it)
        })

        homeViewModel.topTvShowList.observe(viewLifecycleOwner, {
            topTvShowsRowsAdapter(it)
        })

        categoriesRowsAdapter()

        prepareBackgroundManager()
        setupUIElements()
        setupEventListeners()
    }

    private fun initRowsAdapter() {
        val firstHeaderItem = ListRow(HeaderItem(0, AppConstants.TV_CONTINUE_WATCHING), ArrayObjectAdapter(TvCardPresenter()))
        val secondHeaderItem = ListRow(HeaderItem(1, AppConstants.TV_TOP_MOVIES), ArrayObjectAdapter(TvCardPresenter()))
        val thirdHeaderItem = ListRow(HeaderItem(2, AppConstants.TV_TOP_TV_SHOWS), ArrayObjectAdapter(TvCardPresenter()))
        val fourthHeaderItem = ListRow(HeaderItem(3, AppConstants.TV_GENRES), ArrayObjectAdapter(TvCardPresenter()))
        val sixthHeaderItem = ListRow(HeaderItem(5, AppConstants.TV_NEW_MOVIES), ArrayObjectAdapter(TvCardPresenter()))
        val initListRows = mutableListOf(firstHeaderItem, sixthHeaderItem, secondHeaderItem, thirdHeaderItem, fourthHeaderItem)
        rowsAdapter.addAll(0, initListRows)
    }

    private fun watchedListRowsAdapter(dbList: List<DbTitleData>) {
        val listRowAdapter = ArrayObjectAdapter(TvWatchedCardPresenter()).apply {
            dbList.forEach {
                add(it)
            }
        }

        HeaderItem(0, AppConstants.TV_CONTINUE_WATCHING).also {
            rowsAdapter.replace(0, ListRow(it, listRowAdapter))
        }
    }

    private fun newMoviesRowsAdapter(newMovies: List<GetTitlesResponse.Data>) {
        val listRowAdapter = ArrayObjectAdapter(TvCardPresenter()).apply {
            newMovies.forEach {
                add(it)
            }
        }

        HeaderItem(1, AppConstants.TV_NEW_MOVIES).also { header ->
            rowsAdapter.replace(1, ListRow(header, listRowAdapter))
        }
    }

    private fun topMoviesRowsAdapter(movies: List<GetTitlesResponse.Data>) {
        val listRowAdapter = ArrayObjectAdapter(TvCardPresenter()).apply {
            movies.forEach {
                add(it)
            }
        }

        HeaderItem(2, AppConstants.TV_TOP_MOVIES).also { header ->
            rowsAdapter.replace(2, ListRow(header, listRowAdapter))
        }
    }

    private fun topTvShowsRowsAdapter(tvShows: List<GetTitlesResponse.Data>) {
        val listRowAdapter = ArrayObjectAdapter(TvCardPresenter()).apply {
            tvShows.forEach {
                add(it)
            }
        }

        HeaderItem(3, AppConstants.TV_TOP_TV_SHOWS).also { header ->
            rowsAdapter.replace(3, ListRow(header, listRowAdapter))
        }
    }

    private fun categoriesRowsAdapter() {
        val listRowAdapter = ArrayObjectAdapter(TvCategoriesPresenter()).apply {
            add(TvCategoriesList(0, "ტოპ ფილები", R.drawable.tv_top_titles_icon))
            add(TvCategoriesList(1, "ტოპ სერიალები", R.drawable.tv_top_titles_icon))
        }

        HeaderItem(4, AppConstants.TV_GENRES).also { header ->
            rowsAdapter.replace(4, ListRow(header, listRowAdapter))
        }
    }

    private fun prepareBackgroundManager() {
        backgroundManager = BackgroundManager.getInstance(activity).apply {
            attach(activity?.window)
        }
        metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(DisplayMetrics())
    }

    private fun setupUIElements() {
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(requireContext(), R.color.secondary_color)
        adapter = rowsAdapter
    }

    private fun setupEventListeners() {
        onItemViewClickedListener = ItemViewClickedListener()
        onItemViewSelectedListener = ItemViewSelectedListener()
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
                itemViewHolder: Presenter.ViewHolder,
                item: Any,
                rowViewHolder: RowPresenter.ViewHolder,
                row: Row
        ) {
            if (item is GetTitlesResponse.Data) {
                val intent = Intent(context, TvDetailsActivity::class.java)
                intent.putExtra("titleId", item.id)
                intent.putExtra("isTvShow", item.isTvShow)
                activity?.startActivity(intent)
            } else if (item is DbTitleData) {
                val trailerUrl: String? = null
                val intent = Intent(context, TvVideoPlayerActivity::class.java)
                intent.putExtra("videoPlayerData", VideoPlayerData(
                    item.id,
                    item.isTvShow,
                    item.season,
                    item.language,
                    item.episode,
                    item.watchedDuration,
                    trailerUrl
                ))
                activity?.startActivity(intent)
            } else if (item is TvCategoriesList) {
                when (item.categoriesId) {
                    0 -> {
                        val intent = Intent(context, TvCategoriesActivity::class.java)
                        intent.putExtra("type", AppConstants.TV_CATEGORY_TOP_MOVIES)
                        activity?.startActivity(intent)
                    }
                    1 -> {
                        val intent = Intent(context, TvCategoriesActivity::class.java)
                        intent.putExtra("type", AppConstants.TV_CATEGORY_TOP_TV_SHOWS)
                        activity?.startActivity(intent)
                    }
                }
            }
        }
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
            val indexOfItem = ((row as ListRow).adapter as ArrayObjectAdapter).indexOf(item)

            if (item is GetTitlesResponse.Data) {
                onTitleSelected?.getTitleId(item.id, null)
            } else if (item is DbTitleData) {
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