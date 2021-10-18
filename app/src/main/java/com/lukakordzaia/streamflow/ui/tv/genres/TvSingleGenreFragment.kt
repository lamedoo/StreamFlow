package com.lukakordzaia.streamflow.ui.tv.genres

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.leanback.widget.*
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.interfaces.TvCheckFirstItem
import com.lukakordzaia.streamflow.interfaces.TvCheckTitleSelected
import com.lukakordzaia.streamflow.ui.baseclasses.fragments.BaseBrowseSupportFragment
import com.lukakordzaia.streamflow.ui.phone.catalogue.cataloguedetails.SingleCatalogueViewModel
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.TvSingleTitleActivity
import com.lukakordzaia.streamflow.utils.AppConstants
import org.koin.androidx.viewmodel.ext.android.viewModel


class TvSingleGenreFragment : BaseBrowseSupportFragment<SingleCatalogueViewModel>() {
    override val viewModel by viewModel<SingleCatalogueViewModel>()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
//            if (it) {
//                requireContext().createToast(AppConstants.NO_INTERNET)
//                Handler(Looper.getMainLooper()).postDelayed({
//                    viewModel.getSingleGenreForTv(265, 1)
//                    viewModel.getSingleGenreForTv(258, 1)
//                    viewModel.getSingleGenreForTv(260, 1)
//                    viewModel.getSingleGenreForTv(255, 1)
//                    viewModel.getSingleGenreForTv(266, 1)
//                    viewModel.getSingleGenreForTv(248, 1)
//                }, 5000)
//            }
//        })

        viewModel.getSingleGenreForTv(265, 1)
        viewModel.getSingleGenreForTv(258, 1)
        viewModel.getSingleGenreForTv(260, 1)
        viewModel.getSingleGenreForTv(255, 1)
        viewModel.getSingleGenreForTv(266, 1)
        viewModel.getSingleGenreForTv(248, 1)

        viewModel.singleGenreAnimation.observe(viewLifecycleOwner, {
            firstCategoryAdapter(it)
        })

        viewModel.singleGenreComedy.observe(viewLifecycleOwner, {
            secondCategoryAdapter(it)
        })

        viewModel.singleGenreMelodrama.observe(viewLifecycleOwner, {
            thirdCategoryAdapter(it)
        })

        viewModel.singleGenreHorror.observe(viewLifecycleOwner, {
            fourthCategoryAdapter(it)
        })

        viewModel.singleGenreAdventure.observe(viewLifecycleOwner, {
            fifthCategoryAdapter(it)
        })

        viewModel.singleGenreAction.observe(viewLifecycleOwner, {
            sixthCategoryAdapter(it)
        })

        setupEventListeners(ItemViewClickedListener(), ItemViewSelectedListener())
    }

    private fun firstCategoryAdapter(category: List<SingleTitleModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvSingleGenrePresenter(requireContext())).apply {
            category.forEach {
                add(it)
            }
        }

        HeaderItem(0, AppConstants.GENRE_ANIMATION). also {
            rowsAdapter.add(ListRow(it, listRowAdapter))
        }
    }

    private fun secondCategoryAdapter(category: List<SingleTitleModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvSingleGenrePresenter(requireContext())).apply {
            category.forEach {
                add(it)
            }
        }

        HeaderItem(1, AppConstants.GENRE_COMEDY).also { header ->
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }
    }

    private fun thirdCategoryAdapter(category: List<SingleTitleModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvSingleGenrePresenter(requireContext())).apply {
            category.forEach {
                add(it)
            }
        }

        HeaderItem(2, AppConstants.GENRE_MELODRAMA).also { header ->
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }
    }

    private fun fourthCategoryAdapter(category: List<SingleTitleModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvSingleGenrePresenter(requireContext())).apply {
            category.forEach {
                add(it)
            }
        }

        HeaderItem(3, AppConstants.GENRE_HORROR).also { header ->
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }
    }

    private fun fifthCategoryAdapter(category: List<SingleTitleModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvSingleGenrePresenter(requireContext())).apply {
            category.forEach {
                add(it)
            }
        }

        HeaderItem(4, AppConstants.GENRE_ADVENTURE).also { header ->
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }
    }

    private fun sixthCategoryAdapter(category: List<SingleTitleModel>) {
        val listRowAdapter = ArrayObjectAdapter(TvSingleGenrePresenter(requireContext())).apply {
            category.forEach {
                add(it)
            }
        }

        HeaderItem(5, AppConstants.GENRE_ACTION).also { header ->
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
                itemViewHolder: Presenter.ViewHolder,
                item: Any,
                rowViewHolder: RowPresenter.ViewHolder,
                row: Row
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

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
            if (item is SingleTitleModel) {
                onTitleSelected?.getTitleId(item.id, null)
            }

            val indexOfItem = ((row as ListRow).adapter as ArrayObjectAdapter).indexOf(item)

            if (indexOfItem == 0) {
                onFirstItem?.isFirstItem(true, rowsSupportFragment, rowsSupportFragment.selectedPosition)
            } else {
                onFirstItem?.isFirstItem(false, null, null)
            }
        }
    }
}