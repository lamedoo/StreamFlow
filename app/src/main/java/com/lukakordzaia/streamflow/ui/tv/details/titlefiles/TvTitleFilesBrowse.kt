package com.lukakordzaia.streamflow.ui.tv.details.titlefiles

import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.leanback.widget.BrowseFrameLayout.OnFocusSearchListener
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.*
import com.lukakordzaia.streamflow.helpers.CustomListRowPresenter
import com.lukakordzaia.streamflow.ui.tv.details.TvDetailsActivity
import com.lukakordzaia.streamflow.ui.tv.details.titledetails.TvChooseLanguageAdapter
import com.lukakordzaia.streamflow.ui.tv.details.titlefiles.presenters.TvCastPresenter
import com.lukakordzaia.streamflow.ui.tv.details.titlefiles.presenters.TvEpisodesPresenter
import com.lukakordzaia.streamflow.ui.tv.details.titlefiles.presenters.TvSeasonsPresenter
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvCardPresenter
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvHeaderItemPresenter
import com.lukakordzaia.streamflow.ui.tv.tvvideoplayer.TvVideoPlayerActivity
import kotlinx.android.synthetic.main.tv_choose_language_dialog.*
import kotlinx.android.synthetic.main.tv_details_season_item.view.*
import kotlinx.android.synthetic.main.tv_title_files_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvTitleFilesBrowse : BrowseSupportFragment() {
    private val tvTitleFilesViewModel: TvTitleFilesViewModel by viewModel()
    private lateinit var tvChooseLanguageAdapter: TvChooseLanguageAdapter
    private lateinit var rowsAdapter: ArrayObjectAdapter
    lateinit var metrics: DisplayMetrics
    private var hasFocus = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        workaroundFocus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        headersState = HEADERS_DISABLED

        val listRowPresenter = CustomListRowPresenter().apply {
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        val containerDock = view!!.findViewById<View>(R.id.browse_container_dock) as FrameLayout
        val params = containerDock.layoutParams as MarginLayoutParams
        val resources: Resources = inflater.context.resources
        val newHeaderMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, resources.displayMetrics).toInt()
        val offsetToZero: Int = -resources.getDimensionPixelSize(R.dimen.lb_browse_rows_margin_top)
        params.topMargin = offsetToZero + newHeaderMargin
        containerDock.layoutParams = params
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val titleId = activity?.intent?.getSerializableExtra("titleId") as Int
        val isTvShow = activity?.intent?.getSerializableExtra("isTvShow") as Boolean

        initRowsAdapter(isTvShow)

        if (isTvShow) {
            // For Seasons Number
            tvTitleFilesViewModel.getSingleTitleData(titleId)

            tvTitleFilesViewModel.numOfSeasons.observe(viewLifecycleOwner, {
                val seasonCount = Array(it!!) { i -> (i * 1) + 1 }.toList()
                seasonsRowsAdapter(seasonCount)
            })

            tvTitleFilesViewModel.episodeNames.observe(viewLifecycleOwner, {
                episodesRowsAdapter(it)
            })

            if (Firebase.auth.currentUser == null) {
                tvTitleFilesViewModel.checkContinueWatchingTitleInRoom(requireContext(), titleId).observe(viewLifecycleOwner, { exists ->
                    if (exists) {
                        tvTitleFilesViewModel.getSingleContinueWatchingFromRoom(requireContext(), titleId)
                    }
                })
            } else {
                tvTitleFilesViewModel.checkContinueWatchingInFirestore(titleId)
            }
        }

        tvTitleFilesViewModel.getSingleTitleCast(titleId)
        tvTitleFilesViewModel.getSingleTitleRelated(titleId)


        tvTitleFilesViewModel.castData.observe(viewLifecycleOwner, {
            castRowsAdapter(it, isTvShow)
        })

        tvTitleFilesViewModel.singleTitleRelated.observe(viewLifecycleOwner, {
            relatedRowsAdapter(it, isTvShow)
        })

        prepareBackgroundManager()
        setupUIElements()
        onItemViewClickedListener = ItemViewClickedListener()
        onItemViewSelectedListener = ItemViewSelectedListener()
    }

    private fun initRowsAdapter(isTvShow: Boolean) {
        if (isTvShow) {
            val secondHeaderItem = ListRow(HeaderItem(0, "სეზონები"), ArrayObjectAdapter(TvCardPresenter()))
            val firstHeaderItem = ListRow(HeaderItem(1, "ეპიზოდები"), ArrayObjectAdapter(TvCardPresenter()))
            val fourthItem = ListRow(HeaderItem(2, "მსახიობები"), ArrayObjectAdapter(TvCardPresenter()))
            val fifthItem = ListRow(HeaderItem(3, "მსგავსი"), ArrayObjectAdapter(TvCardPresenter()))
            val initListRows = mutableListOf(firstHeaderItem, secondHeaderItem, fourthItem, fifthItem)
            rowsAdapter.addAll(0, initListRows)
        } else {
            val fourthItem = ListRow(HeaderItem(0, "მსახიობები"), ArrayObjectAdapter(TvCardPresenter()))
            val fifthItem = ListRow(HeaderItem(1, "მსგავსი"), ArrayObjectAdapter(TvCardPresenter()))
            val initListRows = mutableListOf(fourthItem, fifthItem)
            rowsAdapter.addAll(0, initListRows)
        }
    }

    private fun seasonsRowsAdapter(seasonList: List<Int>) {
        val listRowAdapter = ArrayObjectAdapter(TvSeasonsPresenter()).apply {
            seasonList.forEach {
                add(it)
            }
        }
        HeaderItem(0, "სეზონები"). also {
            rowsAdapter.replace(0, ListRow(it, listRowAdapter))
        }

        tvTitleFilesViewModel.continueWatchingDetails.observe(viewLifecycleOwner, {
            if (it != null) {
                rowsSupportFragment.setSelectedPosition(0, true, ListRowPresenter.SelectItemViewHolderTask(it.season-1))
            }
        })
    }

    private fun episodesRowsAdapter(episodeList: List<TitleEpisodes>) {
        val listRowAdapter = ArrayObjectAdapter(TvEpisodesPresenter(requireContext())).apply {
            episodeList.forEach {
                add(it)
            }
        }
        HeaderItem(1, "ეპიზოდები"). also {
            rowsAdapter.replace(1, ListRow(it, listRowAdapter))
        }
    }

    private fun castRowsAdapter(castList: List<TitleCast.Data>, isTvShow: Boolean) {
        val listRowAdapter = ArrayObjectAdapter(TvCastPresenter(requireContext())).apply {
            castList.forEach {
                add(it)
            }
        }
        HeaderItem(if (isTvShow) 2 else 0, "მსახიობები"). also {
            rowsAdapter.replace(if (isTvShow) 2 else 0, ListRow(it, listRowAdapter))
        }
    }

    private fun relatedRowsAdapter(relatedList: List<TitleList.Data>, isTvShow: Boolean) {
        val listRowAdapter = ArrayObjectAdapter(TvCardPresenter()).apply {
            relatedList.forEach {
                add(it)
            }
        }
        HeaderItem(if (isTvShow) 3 else 1, "მსგავსი"). also {
            rowsAdapter.replace(if (isTvShow) 3 else 1, ListRow(it, listRowAdapter))
        }
    }

    private fun prepareBackgroundManager() {
        metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
    }

    private fun setupUIElements() {
        title = ""
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(requireContext(), R.color.green_dark)
        searchAffordanceColor = context?.let { ContextCompat.getColor(it, R.color.black) }!!
        adapter = rowsAdapter
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
                itemViewHolder: Presenter.ViewHolder,
                item: Any,
                rowViewHolder: RowPresenter.ViewHolder,
                row: Row
        ) {
            when (item) {
                is TitleEpisodes -> {
                    val titleId = activity?.intent?.getSerializableExtra("titleId") as Int
                    val isTvShow = activity?.intent?.getSerializableExtra("isTvShow") as Boolean

                    val chooseLanguageDialog = Dialog(requireContext())
//                    chooseLanguageDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    chooseLanguageDialog.setContentView(layoutInflater.inflate(R.layout.tv_choose_language_dialog, null))
                    chooseLanguageDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    chooseLanguageDialog.show()

                    val chooseLanguageLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
                    tvChooseLanguageAdapter = TvChooseLanguageAdapter(requireContext()) {
                        playEpisode(titleId, isTvShow, it)
                    }
                    chooseLanguageDialog.rv_tv_choose_language.layoutManager = chooseLanguageLayout
                    chooseLanguageDialog.rv_tv_choose_language.adapter = tvChooseLanguageAdapter

                    tvTitleFilesViewModel.availableLanguages.observe(viewLifecycleOwner, {
                        val languages = it.reversed()
                        tvChooseLanguageAdapter.setLanguageList(languages)
                    })
                }
                is TitleList.Data -> {
                    val intent = Intent(context, TvDetailsActivity::class.java)
                    intent.putExtra("titleId", item.id)
                    intent.putExtra("isTvShow", item.isTvShow)
                    activity?.startActivity(intent)
                }
            }
        }

    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
            if (item is Int) {
                val titleId = activity?.intent?.getSerializableExtra("titleId") as Int
                tvTitleFilesViewModel.getSeasonFiles(titleId, item)
            } else if (item is TitleEpisodes) {
                tvTitleFilesViewModel.getEpisodeFile(item.episodeNum)
            }
        }
    }

    private fun playEpisode(titleId: Int, isTvShow: Boolean, chosenLanguage: String) {
        val trailerUrl: String? = null
        val intent = Intent(context, TvVideoPlayerActivity::class.java)
        intent.putExtra("titleId", titleId)
        intent.putExtra("isTvShow", isTvShow)
        intent.putExtra("chosenLanguage", chosenLanguage)
        intent.putExtra("chosenSeason", this.tvTitleFilesViewModel.chosenSeason.value)
        intent.putExtra("chosenEpisode", this.tvTitleFilesViewModel.chosenEpisode.value)
        intent.putExtra("watchedTime", 0L)
        intent.putExtra("trailerUrl", trailerUrl)
        activity?.startActivity(intent)
    }

    private fun workaroundFocus() {
        if (view != null) {
            val viewToFocus = requireActivity().findViewById<View>(R.id.tv_details_go_top)
            val browseFrameLayout: BrowseFrameLayout = requireView().findViewById(androidx.leanback.R.id.browse_frame)
            browseFrameLayout.onFocusSearchListener = OnFocusSearchListener setOnFocusSearchListener@{ _: View?, direction: Int ->
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