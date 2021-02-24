package com.lukakordzaia.streamflow.ui.tv.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.*
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.DbTitleData
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.datamodels.TvCategoriesList
import com.lukakordzaia.streamflow.datamodels.TvSettingsList
import com.lukakordzaia.streamflow.ui.phone.categories.CategoriesViewModel
import com.lukakordzaia.streamflow.ui.phone.home.HomeViewModel
import com.lukakordzaia.streamflow.ui.phone.profile.ProfileViewModel
import com.lukakordzaia.streamflow.ui.tv.categories.TvCategoriesActivity
import com.lukakordzaia.streamflow.ui.tv.details.TvDetailsActivity
import com.lukakordzaia.streamflow.ui.tv.genres.TvSingleGenreActivity
import com.lukakordzaia.streamflow.ui.tv.main.presenters.*
import com.lukakordzaia.streamflow.ui.tv.search.TvSearchActivity
import com.lukakordzaia.streamflow.ui.tv.tvvideoplayer.TvVideoPlayerActivity
import com.lukakordzaia.streamflow.utils.AppConstants
import com.lukakordzaia.streamflow.utils.EventObserver
import com.lukakordzaia.streamflow.utils.createToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvMainRowsFragment: RowsSupportFragment() {
    private val homeViewModel: HomeViewModel by viewModel()
    private val genresViewModel: CategoriesViewModel by viewModel()
    private val profileViewModel: ProfileViewModel by viewModel()
    private lateinit var rowsAdapter: ArrayObjectAdapter
    private val page = 1
    lateinit var metrics: DisplayMetrics
    lateinit var backgroundManager: BackgroundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
//            prepareEntranceTransition()
        }

//        setHeaderPresenterSelector(object : PresenterSelector() {
//            override fun getPresenter(item: Any?): Presenter {
//                return TvHeaderItemPresenter()
//            }
//        })

        val listRowPresenter = ListRowPresenter().apply {
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
                    homeViewModel.refreshContent(page)
                }, 5000)
            }
        })

        homeViewModel.getContinueWatchingFromRoom(requireContext()).observe(viewLifecycleOwner, {
            homeViewModel.clearContinueWatchingTitleList()
            if (!it.isNullOrEmpty()) {
                homeViewModel.getContinueWatchingTitlesFromApi(it)
            }
        })

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            homeViewModel.getTopMovies(page)
            homeViewModel.getTopTvShows(page)
            homeViewModel.getNewMovies(page)
//            startEntranceTransition()
        }, 2000)

        homeViewModel.dbList.observe(viewLifecycleOwner, {
            watchedListRowsAdapter(it)
        })

        homeViewModel.topMovieList.observe(viewLifecycleOwner, { movies ->
            topMoviesRowsAdapter(movies)
        })

        homeViewModel.newMovieList.observe(viewLifecycleOwner, {
            newMoviesRowsAdapter(it)
        })

        homeViewModel.topTvShowList.observe(viewLifecycleOwner, { tvShows ->
            topTvShowsRowsAdapter(tvShows)
        })

        categoriesRowsAdapter()
        settingsRowsAdapter()

        prepareBackgroundManager()
        setupUIElements()
//        setupEventListeners()
    }

    private fun initRowsAdapter() {
        val firstHeaderItem = ListRow(HeaderItem(0, AppConstants.TV_CONTINUE_WATCHING), ArrayObjectAdapter(TvCardPresenter()))
        val secondHeaderItem = ListRow(HeaderItem(1, AppConstants.TV_TOP_MOVIES), ArrayObjectAdapter(TvCardPresenter()))
        val thirdHeaderItem = ListRow(HeaderItem(2, AppConstants.TV_TOP_TV_SHOWS), ArrayObjectAdapter(TvCardPresenter()))
        val fourthHeaderItem = ListRow(HeaderItem(3, AppConstants.TV_GENRES), ArrayObjectAdapter(TvCardPresenter()))
        val fifthHeaderItem = ListRow(HeaderItem(4, AppConstants.TV_SETTINGS), ArrayObjectAdapter(TvCardPresenter()))
        val sixthHeaderItem = ListRow(HeaderItem(5, AppConstants.TV_NEW_MOVIES), ArrayObjectAdapter(TvCardPresenter()))
        val initListRows = mutableListOf(firstHeaderItem, sixthHeaderItem, secondHeaderItem, thirdHeaderItem, fourthHeaderItem, fifthHeaderItem)
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

    private fun newMoviesRowsAdapter(newMovies: List<TitleList.Data>) {
        val listRowAdapter = ArrayObjectAdapter(TvCardPresenter()).apply {
            newMovies.forEach {
                add(it)
            }
        }

        HeaderItem(1, AppConstants.TV_NEW_MOVIES).also { header ->
            rowsAdapter.replace(1, ListRow(header, listRowAdapter))
        }
    }

    private fun topMoviesRowsAdapter(movies: List<TitleList.Data>) {
        val listRowAdapter = ArrayObjectAdapter(TvCardPresenter()).apply {
            movies.forEach {
                add(it)
            }
        }

        HeaderItem(2, AppConstants.TV_TOP_MOVIES).also { header ->
            rowsAdapter.replace(2, ListRow(header, listRowAdapter))
        }
    }

    private fun topTvShowsRowsAdapter(tvShows: List<TitleList.Data>) {
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
            add(TvCategoriesList(0, "ჟანრის მიხედვით", R.drawable.genre_icon_bottom))
            add(TvCategoriesList(1, "ახალი ფილები", R.drawable.tv_new_movies_icon))
            add(TvCategoriesList(2, "ტოპ ფილები", R.drawable.tv_top_titles_icon))
            add(TvCategoriesList(3, "ტოპ სერიალები", R.drawable.tv_top_titles_icon))
        }

        HeaderItem(4, AppConstants.TV_GENRES).also { header ->
            rowsAdapter.replace(4, ListRow(header, listRowAdapter))
        }
    }

    private fun settingsRowsAdapter() {
        val listRowAdapter = ArrayObjectAdapter(TvSettingsPresenter(requireContext())).apply {
            add(TvSettingsList(0, "ნახვების ისტორიის წაშლა"))
        }

        HeaderItem(5, AppConstants.TV_SETTINGS).also { header ->
            rowsAdapter.replace(5, ListRow(header, listRowAdapter))
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
//        badgeDrawable = resources.getDrawable(R.drawable.streamflowlogo)
//        title = "StreamFlow"
//        headersState = BrowseSupportFragment.HEADERS_ENABLED
//        isHeadersTransitionOnBackEnabled = true
//        brandColor = ContextCompat.getColor(requireContext(), R.color.secondary_color)
//        searchAffordanceColor = context?.let { ContextCompat.getColor(it, R.color.default_background_color) }!!
        adapter = rowsAdapter

    }

    private fun setupEventListeners() {
//        setOnSearchClickedListener {
//            startActivity(Intent(context, TvSearchActivity::class.java))
//        }

        onItemViewClickedListener = ItemViewClickedListener()
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
                itemViewHolder: Presenter.ViewHolder,
                item: Any,
                rowViewHolder: RowPresenter.ViewHolder,
                row: Row
        ) {
            if (item is TitleList.Data) {
                val intent = Intent(context, TvDetailsActivity::class.java)
                intent.putExtra("titleId", item.id)
                intent.putExtra("isTvShow", item.isTvShow)
                activity?.startActivity(intent)
            } else if (item is DbTitleData) {
                val trailerUrl: String? = null
                val intent = Intent(context, TvVideoPlayerActivity::class.java)
                intent.putExtra("titleId", item.id)
                intent.putExtra("isTvShow", item.isTvShow)
                intent.putExtra("chosenLanguage", item.language)
                intent.putExtra("chosenSeason", item.season)
                intent.putExtra("chosenEpisode", item.episode)
                intent.putExtra("watchedTime", item.watchedDuration)
                intent.putExtra("trailerUrl", trailerUrl)
                activity?.startActivity(intent)
            } else if (item is TvSettingsList) {
                if (item.settingsId == 0) {
                    profileViewModel.deleteContinueWatchingFromRoomFull(requireContext())
                    profileViewModel.onDeletePressedTv(requireContext())
                }
            } else if (item is TvCategoriesList) {
                when (item.categoriesId) {
                    0 -> {
                        val intent = Intent(context, TvSingleGenreActivity::class.java)
                        activity?.startActivity(intent)
                    }
                    1 -> {
                        val intent = Intent(context, TvCategoriesActivity::class.java)
                        intent.putExtra("type", AppConstants.TV_CATEGORY_NEW_MOVIES)
                        activity?.startActivity(intent)
                    }
                    2 -> {
                        val intent = Intent(context, TvCategoriesActivity::class.java)
                        intent.putExtra("type", AppConstants.TV_CATEGORY_TOP_MOVIES)
                        activity?.startActivity(intent)
                    }
                    3 -> {
                        val intent = Intent(context, TvCategoriesActivity::class.java)
                        intent.putExtra("type", AppConstants.TV_CATEGORY_TOP_TV_SHOWS)
                        activity?.startActivity(intent)
                    }
                }
            }
        }
    }
}