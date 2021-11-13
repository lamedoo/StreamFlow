package com.lukakrodzaia.streamflowtv.baseclasses

import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.utils.createToast
import com.lukakordzaia.core.utils.setVisibleOrGone
import com.lukakordzaia.streamflow.utils.EventObserver
import com.lukakrodzaia.streamflowtv.R
import com.lukakrodzaia.streamflowtv.helpers.CustomListRowPresenter
import com.lukakrodzaia.streamflowtv.ui.main.presenters.TvHeaderItemPresenter

abstract class BaseBrowseSupportFragment<VM: BaseViewModel>: BrowseSupportFragment() {
    protected abstract val viewModel: VM
    protected lateinit var rowsAdapter: ArrayObjectAdapter
    protected abstract val reload: () -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initHeaderState()
        initRowsAdapter()
        setupUIElements()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initListeners()
    }

    private fun initHeaderState() {
        headersState = HEADERS_DISABLED

        setHeaderPresenterSelector(object : PresenterSelector() {
            override fun getPresenter(item: Any?): Presenter {
                return TvHeaderItemPresenter()
            }
        })
    }

    private fun initRowsAdapter() {
        val listRowPresenter = CustomListRowPresenter().apply {
            shadowEnabled = false
            selectEffectEnabled = false
        }
        rowsAdapter = ArrayObjectAdapter(listRowPresenter)
    }

    private fun setupUIElements() {
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(requireContext(), R.color.secondary_color)
        adapter = rowsAdapter
    }

    fun setupEventListeners(click: OnItemViewClickedListener, select: OnItemViewSelectedListener) {
        onItemViewClickedListener = click
        onItemViewSelectedListener = select
    }

    private fun initObservers() {
        viewModel.noInternet.observe(viewLifecycleOwner, EventObserver {
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
}