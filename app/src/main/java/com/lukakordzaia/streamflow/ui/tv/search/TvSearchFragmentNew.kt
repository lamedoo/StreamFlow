package com.lukakordzaia.streamflow.ui.tv.search

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.network.models.imovies.response.titles.GetTitlesResponse
import com.lukakordzaia.streamflow.helpers.CustomListRowPresenter
import com.lukakordzaia.streamflow.helpers.TvCheckFirstItem
import com.lukakordzaia.streamflow.ui.phone.searchtitles.SearchTitlesViewModel
import com.lukakordzaia.streamflow.ui.tv.details.TvDetailsActivity
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvHeaderItemPresenter
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvSearchFragmentNew : BrowseSupportFragment() {
    private val searchTitlesViewModel by viewModel<SearchTitlesViewModel>()
    private lateinit var rowsAdapter: ArrayObjectAdapter
    private var page = 1
    private var searchQuery = ""
    private var hasFocus = false

    var onFirstItem: TvCheckFirstItem? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onFirstItem = context as? TvCheckFirstItem
    }

    override fun onDetach() {
        super.onDetach()
        onFirstItem = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        workaroundFocus()
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
        onItemViewClickedListener = ItemViewClickedListener()
        onItemViewSelectedListener = ItemViewSelectedListener()

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

        searchTitlesViewModel.searchList.observe(viewLifecycleOwner, { list ->
            Log.d("searchquery", list.toString())
            val listRowAdapter = ArrayObjectAdapter(TvSearchPresenter()).apply {
                list.forEach {
                    add(it)
                }
            }
            rowsAdapter.add(page-1, ListRow(listRowAdapter))

            setupUIElements()
        })
    }

    fun setSearchQuery(query: String) {
        searchQuery = query
        searchTitlesViewModel.getSearchTitlesTv(query, page)
    }

    fun clearRowsAdapter() {
        page = 1
        rowsAdapter.clear()
    }

    fun clearSearchResults() {
        searchTitlesViewModel.clearSearchResults()
    }

    private fun setupUIElements() {
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(requireContext(), R.color.secondary_color)
        adapter = rowsAdapter
    }


    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(itemViewHolder: Presenter.ViewHolder, item: Any, rowViewHolder: RowPresenter.ViewHolder, row: Row) {
            if (item is GetTitlesResponse.Data) {
                val intent = Intent(context, TvDetailsActivity::class.java)
                intent.putExtra("titleId", item.id)
                intent.putExtra("isTvShow", item.isTvShow)
                activity?.startActivity(intent)
            }
        }
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
            val listRow = row as ListRow
            val currentRowAdapter: ArrayObjectAdapter = listRow.adapter as ArrayObjectAdapter
            val selectedIndex = currentRowAdapter.indexOf(item)

            if (selectedIndex <= 0) {
                onFirstItem?.isFirstItem(true, rowsSupportFragment, rowsSupportFragment?.selectedPosition)
            } else {
                onFirstItem?.isFirstItem(false, null, null)
            }

            if (selectedIndex != -1 && currentRowAdapter.size() - 1 == selectedIndex) {
                page++
                searchTitlesViewModel.getSearchTitlesTv(searchQuery, page)
                Log.d("lastitem", "true")
                rowsSupportFragment!!.setSelectedPosition(1, true, ListRowPresenter.SelectItemViewHolderTask(0))
            }
        }
    }

    private fun workaroundFocus() {
        if (view != null) {
            val viewToFocus = requireActivity().findViewById<View>(R.id.tv_search_title_text)
            val browseFrameLayout: BrowseFrameLayout = requireView().findViewById(androidx.leanback.R.id.browse_frame)
            browseFrameLayout.onFocusSearchListener = BrowseFrameLayout.OnFocusSearchListener setOnFocusSearchListener@{ _: View?, direction: Int ->
                if (direction == View.FOCUS_UP) {
                    this.hasFocus = true
                    return@setOnFocusSearchListener viewToFocus
                } else {
                    return@setOnFocusSearchListener null
                }
            }
        }
    }
}