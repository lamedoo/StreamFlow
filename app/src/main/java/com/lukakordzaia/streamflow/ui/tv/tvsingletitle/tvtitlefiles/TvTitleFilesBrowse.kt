package com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitlefiles

import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.databinding.DialogChooseLanguageBinding
import com.lukakordzaia.streamflow.datamodels.*
import com.lukakordzaia.streamflow.helpers.CustomListRowPresenter
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleCastResponse
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvCardPresenter
import com.lukakordzaia.streamflow.ui.tv.main.presenters.TvHeaderItemPresenter
import com.lukakordzaia.streamflow.ui.tv.search.TvSearchPresenter
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.TvSingleTitleActivity
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitledetails.TvChooseLanguageAdapter
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitlefiles.presenters.TvCastPresenter
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitlefiles.presenters.TvEpisodesPresenter
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitlefiles.presenters.TvSeasonsPresenter
import com.lukakordzaia.streamflow.ui.tv.tvvideoplayer.TvVideoPlayerActivity
import kotlinx.android.synthetic.main.tv_details_season_item.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvTitleFilesBrowse : BrowseSupportFragment() {
    private val tvTitleFilesViewModel: TvTitleFilesViewModel by viewModel()
    private lateinit var tvChooseLanguageAdapter: TvChooseLanguageAdapter
    private lateinit var rowsAdapter: ArrayObjectAdapter
    lateinit var metrics: DisplayMetrics
    private var hasFocus = false

    private var focusedSeason = 0

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
        val titleId: Int?
        val isTvShow: Boolean?

        val titleIdFromDetails = activity?.intent?.getSerializableExtra("titleId") as? Int
        val isTvShowFromDetails = activity?.intent?.getSerializableExtra("isTvShow") as? Boolean
        val videoPlayerData = activity?.intent?.getParcelableExtra("videoPlayerData") as? VideoPlayerData

        if (titleIdFromDetails == null) {
            titleId = videoPlayerData?.titleId
            isTvShow = videoPlayerData?.isTvShow
        } else {
            titleId = titleIdFromDetails
            isTvShow = isTvShowFromDetails
        }

        initRowsAdapter(isTvShow!!)
        setSeasonsAndEpisodes(titleId!!, isTvShow)
        setTitleCast(titleId, isTvShow)
        setTitleRelated(titleId, isTvShow)

        prepareBackgroundManager()
        setupUIElements()
        onItemViewClickedListener = ItemViewClickedListener(titleId, isTvShow)
        onItemViewSelectedListener = ItemViewSelectedListener(titleId)
    }

    private fun initRowsAdapter(isTvShow: Boolean) {
        if (isTvShow) {
            val secondHeaderItem = ListRow(HeaderItem(0, "სეზონები"), ArrayObjectAdapter(TvCardPresenter(requireContext())))
            val firstHeaderItem = ListRow(HeaderItem(1, "ეპიზოდები"), ArrayObjectAdapter(TvCardPresenter(requireContext())))
            val fourthItem = ListRow(HeaderItem(2, "მსახიობები"), ArrayObjectAdapter(TvCardPresenter(requireContext())))
            val fifthItem = ListRow(HeaderItem(3, "მსგავსი"), ArrayObjectAdapter(TvCardPresenter(requireContext())))
            val initListRows = mutableListOf(firstHeaderItem, secondHeaderItem, fourthItem, fifthItem)
            rowsAdapter.addAll(0, initListRows)
        } else {
            val fourthItem = ListRow(HeaderItem(0, "მსახიობები"), ArrayObjectAdapter(TvCardPresenter(requireContext())))
            val fifthItem = ListRow(HeaderItem(1, "მსგავსი"), ArrayObjectAdapter(TvCardPresenter(requireContext())))
            val initListRows = mutableListOf(fourthItem, fifthItem)
            rowsAdapter.addAll(0, initListRows)
        }
    }

    private fun setSeasonsAndEpisodes(titleId: Int, isTvShow: Boolean) {
        if (isTvShow) {
            tvTitleFilesViewModel.getSingleTitleData(titleId)
            tvTitleFilesViewModel.checkAuthDatabase(titleId)

            tvTitleFilesViewModel.numOfSeasons.observe(viewLifecycleOwner, {
                val seasonCount = Array(it!!) { i -> (i * 1) + 1 }.toList()
                seasonsRowsAdapter(seasonCount)
            })

            episodesRowsAdapter(null)
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
                focusedSeason = it.season
                episodesRowsAdapter(it.episode, it.season)
            }
        })
    }

    private fun episodesRowsAdapter(currentEpisode: Int?, currentSeason: Int? = null) {
        var isFirst = true

        tvTitleFilesViewModel.episodeNames.observe(viewLifecycleOwner, { episodeList ->
            val listRowAdapter = ArrayObjectAdapter(TvEpisodesPresenter(requireContext(), currentEpisode, currentSeason == focusedSeason)).apply {
                episodeList.forEach {
                    add(it)
                }
            }

            HeaderItem(1, "ეპიზოდები"). also {
                rowsAdapter.replace(1, ListRow(it, listRowAdapter))
            }

            if (currentEpisode != null && isFirst) {
                Handler(Looper.myLooper()!!).postDelayed({
                    rowsSupportFragment.setSelectedPosition(1, true, ListRowPresenter.SelectItemViewHolderTask(currentEpisode-1))
                }, 1000)
                isFirst = false
            }
        })
    }

    private fun castRowsAdapter(castResponseListGetSingle: List<GetSingleTitleCastResponse.Data>, isTvShow: Boolean) {
        val listRowAdapter = ArrayObjectAdapter(TvCastPresenter(requireContext())).apply {
            castResponseListGetSingle.forEach {
                add(it)
            }
        }
        HeaderItem(if (isTvShow) 2 else 0, "მსახიობები"). also {
            rowsAdapter.replace(if (isTvShow) 2 else 0, ListRow(it, listRowAdapter))
        }
    }

    private fun relatedRowsAdapter(relatedList: List<SingleTitleModel>, isTvShow: Boolean) {
        val listRowAdapter = ArrayObjectAdapter(TvSearchPresenter()).apply {
            addAll(0, relatedList)
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

    private fun setTitleCast(titleId: Int, isTvShow: Boolean) {
        tvTitleFilesViewModel.getSingleTitleCast(titleId)

        tvTitleFilesViewModel.castResponseDataGetSingle.observe(viewLifecycleOwner, {
            castRowsAdapter(it, isTvShow)
        })
    }

    private fun setTitleRelated(titleId: Int, isTvShow: Boolean) {
        tvTitleFilesViewModel.getSingleTitleRelated(titleId)

        tvTitleFilesViewModel.singleTitleRelated.observe(viewLifecycleOwner, {
            relatedRowsAdapter(it, isTvShow)
        })
    }

    private inner class ItemViewClickedListener(val titleId: Int, val isTvShow: Boolean) : OnItemViewClickedListener {
        override fun onItemClicked(
                itemViewHolder: Presenter.ViewHolder,
                item: Any,
                rowViewHolder: RowPresenter.ViewHolder,
                row: Row
        ) {
            when (item) {
                is TitleEpisodes -> {
                    val binding = DialogChooseLanguageBinding.inflate(LayoutInflater.from(requireContext()))
                    val chooseLanguageDialog = Dialog(requireContext())
                    chooseLanguageDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    chooseLanguageDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    chooseLanguageDialog.setContentView(binding.root)
                    chooseLanguageDialog.show()

                    val chooseLanguageLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
                    tvChooseLanguageAdapter = TvChooseLanguageAdapter(requireContext()) {
                        chooseLanguageDialog.hide()
                        playEpisode(titleId, isTvShow, it)
                    }
                    binding.rvChooseLanguage.layoutManager = chooseLanguageLayout
                    binding.rvChooseLanguage.adapter = tvChooseLanguageAdapter

                    tvTitleFilesViewModel.availableLanguages.observe(viewLifecycleOwner, {
                        val languages = it.reversed()
                        tvChooseLanguageAdapter.setLanguageList(languages)
                    })
                }
                is SingleTitleModel -> {
                    val intent = Intent(context, TvSingleTitleActivity::class.java).apply {
                        putExtra("titleId", item.id)
                        putExtra("isTvShow", item.isTvShow)
                    }
                    requireActivity().startActivity(intent)
                    if (requireActivity() is TvVideoPlayerActivity) {
                        requireActivity().finish()
                    }
                }
            }
        }
    }

    private inner class ItemViewSelectedListener(val titleId: Int) : OnItemViewSelectedListener {
        override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
            if (item is Int) {
                tvTitleFilesViewModel.getSeasonFiles(titleId, item)
                focusedSeason = item
            } else if (item is TitleEpisodes) {
                tvTitleFilesViewModel.getEpisodeFile(item.episodeNum)
            }
        }
    }

    private fun playEpisode(titleId: Int, isTvShow: Boolean, chosenLanguage: String) {
        val trailerUrl: String? = null
        val intent = Intent(context, TvVideoPlayerActivity::class.java)
        intent.putExtra("videoPlayerData", VideoPlayerData(
            titleId,
            isTvShow,
            tvTitleFilesViewModel.chosenSeason.value!!,
            chosenLanguage,
            tvTitleFilesViewModel.chosenEpisode.value!!,
            0L,
            trailerUrl
        ))
        requireActivity().startActivity(intent)
        if (requireActivity() is TvVideoPlayerActivity) {
            requireActivity().finish()
        }
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