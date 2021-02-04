package com.lukakordzaia.imoviesapp.ui.tv.main

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.ProgressBarManager
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.datamodels.*
import com.lukakordzaia.imoviesapp.ui.phone.genres.GenresViewModel
import com.lukakordzaia.imoviesapp.ui.phone.home.HomeViewModel
import com.lukakordzaia.imoviesapp.ui.phone.settings.SettingsViewModel
import com.lukakordzaia.imoviesapp.ui.tv.details.TvDetailsActivity
import com.lukakordzaia.imoviesapp.ui.tv.genres.TvSingleGenreActivity
import com.lukakordzaia.imoviesapp.ui.tv.search.TvSearchActivity
import com.lukakordzaia.imoviesapp.ui.tv.tvvideoplayer.TvVideoPlayerActivity
import com.lukakordzaia.imoviesapp.utils.AppConstants
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvMainFragment : BrowseSupportFragment() {
    private val homeViewModel by viewModel<HomeViewModel>()
    private val genresViewModel by viewModel<GenresViewModel>()
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var rowsAdapter: ArrayObjectAdapter
    lateinit var defaultBackground: Drawable
    lateinit var metrics: DisplayMetrics
    lateinit var backgroundManager: BackgroundManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        val listRowPresenter = ListRowPresenter().apply {
            shadowEnabled = false
            selectEffectEnabled = false
        }
        rowsAdapter = ArrayObjectAdapter(listRowPresenter)

        homeViewModel.getDbTitles(requireContext()).observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                homeViewModel.getDbTitlesFromApi(it)
            }
        })

        homeViewModel.getTopMovies(1)
        homeViewModel.getTopTvShows(1)
        homeViewModel.getNewMovies(1)
        genresViewModel.getAllGenres()


        setHeaderPresenterSelector(object : PresenterSelector() {
            override fun getPresenter(item: Any?): Presenter {
                return TvHeaderItemPresenter()
            }
        })

        initRowsAdapter()

        homeViewModel.watchedList.observe(viewLifecycleOwner, {
                watchedListRowsAdapter(it)
        })

        homeViewModel.topMovieList.observe(viewLifecycleOwner, { movies ->
            topMoviesRowsAdapter(movies)
        })

        homeViewModel.newMovieList.observe(viewLifecycleOwner, {
            newMoviesRowsAdapter(it)
        })

        homeViewModel.tvShowList.observe(viewLifecycleOwner, { tvShows ->
            topTvShowsRowsAdapter(tvShows)
        })

        genresViewModel.allGenresList.observe(viewLifecycleOwner, { genres ->
            genresRowsAdapter(genres)
        })

        settingsRowsAdapter()

        prepareBackgroundManager()
        setupUIElements()
        setupEventListeners()
    }

    private fun initRowsAdapter() {
        val firstHeaderItem = ListRow(HeaderItem(0, AppConstants.TV_CONTINUE_WATCHING), ArrayObjectAdapter(TvCardPresenter()))
        val secondHeaderItem = ListRow(HeaderItem(1, AppConstants.TV_TOP_MOVIES), ArrayObjectAdapter(TvCardPresenter()))
        val thirdHeaderItem = ListRow(HeaderItem(2, AppConstants.TV_TOP_TV_SHOWS), ArrayObjectAdapter(TvCardPresenter()))
        val fourthHeaderItem = ListRow(HeaderItem(3, AppConstants.TV_GENRES), ArrayObjectAdapter(TvCardPresenter()))
        val fifthHeaderItem = ListRow(HeaderItem(4, AppConstants.TV_SETTINGS), ArrayObjectAdapter(TvCardPresenter()))
        val sixthHeaderItem = ListRow(HeaderItem(5, AppConstants.TV_NEW_MOVIES), ArrayObjectAdapter(TvCardPresenter()))
        val initListRows = mutableListOf(firstHeaderItem, secondHeaderItem, sixthHeaderItem, thirdHeaderItem, fourthHeaderItem, fifthHeaderItem)
        rowsAdapter.addAll(0, initListRows)
    }

    private fun watchedListRowsAdapter(watchedList: List<WatchedTitleData>) {
        val listRowAdapter = ArrayObjectAdapter(TvWatchedCardPresenter()).apply {
            watchedList.forEach {
                add(it)
            }
        }

        HeaderItem(0, AppConstants.TV_CONTINUE_WATCHING). also {
            rowsAdapter.replace(0, ListRow(it, listRowAdapter))
        }
    }

    private fun topMoviesRowsAdapter(movies: List<TitleList.Data>) {
        val listRowAdapter = ArrayObjectAdapter(TvCardPresenter()).apply {
            movies.forEach {
                add(it)
            }
        }

        HeaderItem(1, AppConstants.TV_TOP_MOVIES).also { header ->
            rowsAdapter.replace(1, ListRow(header, listRowAdapter))
        }
    }

    private fun newMoviesRowsAdapter(newMovies: List<TitleList.Data>) {
        val listRowAdapter = ArrayObjectAdapter(TvCardPresenter()).apply {
            newMovies.forEach {
                add(it)
            }
        }

        HeaderItem(2, AppConstants.TV_NEW_MOVIES).also { header ->
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

    private fun genresRowsAdapter(genreList: List<GenreList.Data>) {
        val listRowAdapter = ArrayObjectAdapter(TvCategoriesPresenter()).apply {
            add(TvCategoriesList(0, "ჟანრის მიხედვით", R.drawable.tv_genre_icon))
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
        defaultBackground = resources.getDrawable(R.drawable.main_background)
        metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
    }

    private fun setupUIElements() {
        badgeDrawable = resources.getDrawable(R.drawable.imovies_logo)
        title = "IMOVIES"
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(requireContext(), R.color.green_dark)
        searchAffordanceColor = context?.let { ContextCompat.getColor(it, R.color.black) }!!
        adapter = rowsAdapter

    }

    private fun setupEventListeners() {
        setOnSearchClickedListener {
            startActivity(Intent(context, TvSearchActivity::class.java))
        }

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
            } else if (item is WatchedTitleData) {
                val trailerUrl: String? = null
                val intent = Intent(context, TvVideoPlayerActivity::class.java)
                intent.putExtra("titleId", item.id)
                intent.putExtra("isTvShow", item.isTvShow)
                intent.putExtra("chosenLanguage", item.language)
                intent.putExtra("chosenSeason", item.season)
                intent.putExtra("chosenEpisode", item.episode)
                intent.putExtra("watchedTime", item.watchedTime)
                intent.putExtra("trailerUrl", trailerUrl)
                activity?.startActivity(intent)
            } else if (item is TvSettingsList) {
                if (item.settingsId == 0) {
                    settingsViewModel.deleteWatchedHistory(requireContext())
                    settingsViewModel.onDeletePressedTv(requireContext())
                }
            } else if (item is TvCategoriesList) {
                val intent = Intent(context, TvSingleGenreActivity::class.java)
                activity?.startActivity(intent)
            }
        }
    }
}