package com.lukakordzaia.streamflow.ui.tv.genres

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.ui.phone.categories.singlegenre.SingleCategoryViewModel
import com.lukakordzaia.streamflow.ui.tv.details.TvDetailsActivity
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvCardPresenter
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvHeaderItemPresenter
import com.lukakordzaia.streamflow.utils.AppConstants
import com.lukakordzaia.streamflow.utils.EventObserver
import com.lukakordzaia.streamflow.utils.createToast
import org.koin.androidx.viewmodel.ext.android.viewModel


class TvSingleGenreFragment : BrowseSupportFragment() {
    private val singleGenreViewModel by viewModel<SingleCategoryViewModel>()
    private lateinit var rowsAdapter: ArrayObjectAdapter
    lateinit var defaultBackground: Drawable
    lateinit var metrics: DisplayMetrics
    lateinit var backgroundManager: BackgroundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        headersState = HEADERS_DISABLED

        val listRowPresenter = ListRowPresenter().apply {
            shadowEnabled = false
            selectEffectEnabled = false
        }
        rowsAdapter = ArrayObjectAdapter(listRowPresenter)

        setHeaderPresenterSelector(object : PresenterSelector() {
            override fun getPresenter(item: Any?): Presenter {
                return TvHeaderItemPresenter()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        singleGenreViewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                requireContext().createToast(AppConstants.NO_INTERNET)
                Handler(Looper.getMainLooper()).postDelayed({
                    singleGenreViewModel.getSingleGenreForTv(265, 1)
                    singleGenreViewModel.getSingleGenreForTv(258, 1)
                    singleGenreViewModel.getSingleGenreForTv(260, 1)
                    singleGenreViewModel.getSingleGenreForTv(255, 1)
                    singleGenreViewModel.getSingleGenreForTv(266, 1)
                    singleGenreViewModel.getSingleGenreForTv(248, 1)
                }, 5000)
            }
        })

        singleGenreViewModel.getSingleGenreForTv(265, 1)
        singleGenreViewModel.getSingleGenreForTv(258, 1)
        singleGenreViewModel.getSingleGenreForTv(260, 1)
        singleGenreViewModel.getSingleGenreForTv(255, 1)
        singleGenreViewModel.getSingleGenreForTv(266, 1)
        singleGenreViewModel.getSingleGenreForTv(248, 1)

        singleGenreViewModel.singleGenreAnimation.observe(viewLifecycleOwner, {
            firstCategoryAdapter(it)
        })

        singleGenreViewModel.singleGenreComedy.observe(viewLifecycleOwner, {
            secondCategoryAdapter(it)
        })

        singleGenreViewModel.singleGenreMelodrama.observe(viewLifecycleOwner, {
            thirdCategoryAdapter(it)
        })

        singleGenreViewModel.singleGenreHorror.observe(viewLifecycleOwner, {
            fourthCategoryAdapter(it)
        })

        singleGenreViewModel.singleGenreAdventure.observe(viewLifecycleOwner, {
            fifthCategoryAdapter(it)
        })

        singleGenreViewModel.singleGenreAction.observe(viewLifecycleOwner, {
            sixthCategoryAdapter(it)
        })

        prepareBackgroundManager()
        setupUIElements()
        setupEventListeners()

        singleGenreViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })
    }

    private fun firstCategoryAdapter(category: List<TitleList.Data>) {
        val listRowAdapter = ArrayObjectAdapter(TvSingleGenrePresenter()).apply {
            category.forEach {
                add(it)
            }
        }

        HeaderItem(0, AppConstants.GENRE_ANIMATION). also {
            rowsAdapter.add(ListRow(it, listRowAdapter))
        }
    }

    private fun secondCategoryAdapter(category: List<TitleList.Data>) {
        val listRowAdapter = ArrayObjectAdapter(TvSingleGenrePresenter()).apply {
            category.forEach {
                add(it)
            }
        }

        HeaderItem(1, AppConstants.GENRE_COMEDY).also { header ->
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }
    }

    private fun thirdCategoryAdapter(category: List<TitleList.Data>) {
        val listRowAdapter = ArrayObjectAdapter(TvSingleGenrePresenter()).apply {
            category.forEach {
                add(it)
            }
        }

        HeaderItem(2, AppConstants.GENRE_MELODRAMA).also { header ->
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }
    }

    private fun fourthCategoryAdapter(category: List<TitleList.Data>) {
        val listRowAdapter = ArrayObjectAdapter(TvSingleGenrePresenter()).apply {
            category.forEach {
                add(it)
            }
        }

        HeaderItem(3, AppConstants.GENRE_HORROR).also { header ->
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }
    }

    private fun fifthCategoryAdapter(category: List<TitleList.Data>) {
        val listRowAdapter = ArrayObjectAdapter(TvSingleGenrePresenter()).apply {
            category.forEach {
                add(it)
            }
        }

        HeaderItem(4, AppConstants.GENRE_ADVENTURE).also { header ->
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }
    }

    private fun sixthCategoryAdapter(category: List<TitleList.Data>) {
        val listRowAdapter = ArrayObjectAdapter(TvSingleGenrePresenter()).apply {
            category.forEach {
                add(it)
            }
        }

        HeaderItem(5, AppConstants.GENRE_ACTION).also { header ->
            rowsAdapter.add(ListRow(header, listRowAdapter))
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
        badgeDrawable = resources.getDrawable(R.drawable.tv_genre_icon_full)
        title = "MedooTV"
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(requireContext(), R.color.secondary_color)
        adapter = rowsAdapter
    }

    private fun setupEventListeners() {

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
            }
        }
    }
}