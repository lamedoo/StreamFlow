package com.lukakordzaia.streamflowtv.baseclasses

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.utils.createToast
import com.lukakordzaia.core.utils.setVisibleOrGone
import com.lukakordzaia.core.utils.EventObserver
import com.lukakordzaia.streamflowtv.R
import com.lukakordzaia.streamflowtv.interfaces.TvCheckFirstItem
import com.lukakordzaia.streamflowtv.interfaces.TvTitleSelected
import com.lukakordzaia.streamflowtv.ui.tvcatalogue.TvCataloguePresenter

abstract class BaseVerticalGridSupportFragment<VM: BaseViewModel> : VerticalGridSupportFragment() {
    protected abstract val viewModel: VM
    protected var page = 1
    protected lateinit var gridAdapter: ArrayObjectAdapter
    protected abstract val reload: () -> Unit

    protected var onTitleSelected: TvTitleSelected? = null
    protected var onFirstItem: TvCheckFirstItem? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onTitleSelected = context as? TvTitleSelected
        onFirstItem = context as? TvCheckFirstItem
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initGridPresenter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initListeners()
        initGridAdapter()
    }

    private fun initObservers() {
        viewModel.noInternet.observe(viewLifecycleOwner, {
            requireActivity().findViewById<ConstraintLayout>(R.id.no_internet).setVisibleOrGone(it)
        })

        viewModel.generalLoader.observe(viewLifecycleOwner, {
            when (it) {
                LoadingState.LOADING -> prepareEntranceTransition()
                LoadingState.LOADED -> startEntranceTransition()
            }
        })

        viewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })
    }

    private fun initListeners() {
        requireActivity().findViewById<Button>(R.id.retry_button)?.setOnClickListener {
            reload.invoke()
        }
    }

    private fun initGridPresenter() {
        title = ""
        val gridPresenter = VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_NONE, false)
        gridPresenter.numberOfColumns = 6
        setGridPresenter(gridPresenter)
    }

    private fun initGridAdapter() {
        gridAdapter = ArrayObjectAdapter(TvCataloguePresenter())
        adapter = gridAdapter
    }

    fun setupEventListeners(click: OnItemViewClickedListener, select: OnItemViewSelectedListener) {
        onItemViewClickedListener = click
        setOnItemViewSelectedListener(select)
    }

    override fun onDetach() {
        super.onDetach()
        onTitleSelected = null
        onFirstItem = null
    }
}