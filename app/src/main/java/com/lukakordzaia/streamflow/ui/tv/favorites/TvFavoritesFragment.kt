package com.lukakordzaia.streamflow.ui.tv.favorites

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
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
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.helpers.CustomListRowPresenter
import com.lukakordzaia.streamflow.interfaces.TvCheckFirstItem
import com.lukakordzaia.streamflow.interfaces.TvCheckTitleSelected
import com.lukakordzaia.streamflow.interfaces.TvHasFavoritesListener
import com.lukakordzaia.streamflow.ui.phone.favorites.PhoneFavoritesViewModel
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvHeaderItemPresenter
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.TvSingleTitleActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvFavoritesFragment : BrowseSupportFragment() {
    private val phoneFavoritesViewModel: PhoneFavoritesViewModel by viewModel()
    private lateinit var rowsAdapter: ArrayObjectAdapter
    lateinit var metrics: DisplayMetrics
    lateinit var backgroundManager: BackgroundManager

    var onTitleSelected: TvCheckTitleSelected? = null
    var onFirstItem: TvCheckFirstItem? = null
    var hasFavorites: TvHasFavoritesListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onTitleSelected = context as? TvCheckTitleSelected
        onFirstItem = context as? TvCheckFirstItem
        hasFavorites = context as? TvHasFavoritesListener
    }

    override fun onDetach() {
        super.onDetach()
        onTitleSelected = null
        onFirstItem = null
        hasFavorites = null
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
        phoneFavoritesViewModel.getFavTitlesFromFirestore()

        initRowsAdapter()

        phoneFavoritesViewModel.favoriteNoMovies.observe(viewLifecycleOwner, { noMovies ->
            phoneFavoritesViewModel.favoriteNoTvShows.observe(viewLifecycleOwner, { noTvShows ->
                if (noMovies && noTvShows) {
                    hasFavorites?.hasFavorites(false)
                }
            })
        })

        phoneFavoritesViewModel.movieResult.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                movieRowsAdapter(it)
            }
        })

        phoneFavoritesViewModel.tvShowResult.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                tvShowsRowsAdapter(it)
            }
        })

        prepareBackgroundManager()
        setupUIElements()
        setupEventListeners()
    }

    private fun initRowsAdapter() {
        val firstHeaderItem = ListRow(HeaderItem(0, ""), ArrayObjectAdapter(TvFavoritesPresenter()))
        val secondHeaderItem = ListRow(HeaderItem(1, ""), ArrayObjectAdapter(TvFavoritesPresenter()))
        val initListRows = mutableListOf(firstHeaderItem, secondHeaderItem)
        rowsAdapter.addAll(0, initListRows)
    }

    private fun movieRowsAdapter(movies: List<SingleTitleModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvFavoritesPresenter()).apply {
            addAll(0, movies)
        }

        HeaderItem(0, "ფილმები").also {
            rowsAdapter.replace(0, ListRow(it, listRowAdapter))
        }

        startEntranceTransition()
    }

    private fun tvShowsRowsAdapter(tvShows: List<SingleTitleModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvFavoritesPresenter()).apply {
            addAll(0, tvShows)
        }

        HeaderItem(1, "სერიალები").also {
            rowsAdapter.replace(1, ListRow(it, listRowAdapter))
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
            if (item is SingleTitleModel) {
                val intent = Intent(context, TvSingleTitleActivity::class.java)
                intent.putExtra("titleId", item.id)
                intent.putExtra("isTvShow", item.isTvShow)
                activity?.startActivity(intent)
            }
        }
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
            val indexOfItem = ((row as ListRow).adapter as ArrayObjectAdapter).indexOf(item)

            if (item is SingleTitleModel) {
                onTitleSelected?.getTitleId(item.id, null)
            }

            if (indexOfItem == 0) {
                onFirstItem?.isFirstItem(true, rowsSupportFragment, rowsSupportFragment.selectedPosition)
            } else {
                onFirstItem?.isFirstItem(false, null, null)
            }
        }
    }
}