package com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvepisodes

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import com.lukakordzaia.core.adapters.ChooseLanguageAdapter
import com.lukakordzaia.core.databinding.DialogChooseLanguageBinding
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.datamodels.SingleTitleModel
import com.lukakordzaia.core.datamodels.TitleEpisodes
import com.lukakordzaia.core.datamodels.VideoPlayerData
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.streamflowtv.baseclasses.BaseVerticalGridSupportFragment
import com.lukakordzaia.streamflowtv.interfaces.TvCheckFirstItem
import com.lukakordzaia.streamflowtv.interfaces.TvTitleSelected
import com.lukakordzaia.streamflowtv.ui.tvcatalogue.TvCataloguePresenter
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.TvSingleTitleActivity
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitlerelated.presenters.TvEpisodesPresenter
import com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitlerelated.presenters.TvSeasonsPresenter
import com.lukakordzaia.streamflowtv.ui.tvvideoplayer.TvVideoPlayerActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class TvEpisodesBrowse : VerticalGridSupportFragment() {
    var titleId: Int = 0

    val viewModel by viewModel<TvEpisodesViewModel>()
    private lateinit var gridAdapter: ArrayObjectAdapter

    private var onTitleSelected: TvTitleSelected? = null
    private var onFirstItem: TvCheckFirstItem? = null

    private lateinit var chooseLanguageAdapter: ChooseLanguageAdapter

    private var continueSeason = 1
    private var continueEpisode = 1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onTitleSelected = context as? TvTitleSelected
        onFirstItem = context as? TvCheckFirstItem

        val args = arguments
        continueSeason = args?.getInt("season") ?: 1
        continueEpisode = args?.getInt("episode") ?: 1

        val titleId = activity?.intent?.getSerializableExtra(AppConstants.TITLE_ID) as? Int
        val videoPlayerData = activity?.intent?.getParcelableExtra(AppConstants.VIDEO_PLAYER_DATA) as? VideoPlayerData

        this.titleId = titleId ?: videoPlayerData!!.titleId
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initGridPresenter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSeasonFiles(this.titleId, continueSeason)
        fragmentObservers()
        setupEventListeners(ItemViewClickedListener(), ItemViewSelectedListener())

        initGridAdapter()
    }

    private fun fragmentObservers() {
        viewModel.episodeNames.observe(viewLifecycleOwner, {
            gridAdapter.clear()
            gridAdapter.addAll(0, it)


            if ((parentFragment as TvEpisodesFragment).isEpisodeFirstSelection()) {
                setSelectedPosition(continueEpisode - 1)
                (parentFragment as TvEpisodesFragment).setFragmentFocus("episode")
                (parentFragment as TvEpisodesFragment).setEpisodeFirstSelection(false)
            }
        })
    }

    private fun initGridPresenter() {
        title = ""
        val gridPresenter = StandardGridPresenter(FocusHighlight.ZOOM_FACTOR_NONE, false)
        gridPresenter.numberOfColumns = 1
        gridPresenter.shadowEnabled = false
        setGridPresenter(gridPresenter)
    }

    private fun initGridAdapter() {
        gridAdapter = ArrayObjectAdapter(TvEpisodesPresenter(requireContext()))
        adapter = gridAdapter
    }

    private fun setupEventListeners(click: OnItemViewClickedListener, select: OnItemViewSelectedListener) {
        onItemViewClickedListener = click
        setOnItemViewSelectedListener(select)
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder?,
            item: Any?,
            rowViewHolder: RowPresenter.ViewHolder?,
            row: Row?
        ) {
            if (item is TitleEpisodes) {
                viewModel.getEpisodeLanguages(item.titleId, item.episodeNum)
                languagePickerDialog(item.episodeNum)
            }
        }
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
            val indexOfRow = gridAdapter.size()
            val indexOfItem = gridAdapter.indexOf(item)

            if (item is TitleEpisodes) {
                continueEpisode = 1
            }

            if (item is SingleTitleModel) {
                onTitleSelected?.getTitleId(item.id, null)
            }

            val gridSize = Array(gridAdapter.size()) { i -> (i * 1) + 1 }.toList()

            onFirstItem?.isFirstItem(false, null, null)

            if (indexOfItem == 0) {
                onFirstItem?.isFirstItem(true, null, null)
            }

            gridSize.forEach {
                if (it % 6 == 0) {
                    if (indexOfItem == it) {
                        onFirstItem?.isFirstItem(true, null, null)
                    }
                }
            }
        }
    }

    private fun languagePickerDialog(episode: Int) {
        val binding = DialogChooseLanguageBinding.inflate(LayoutInflater.from(requireContext()))
        val chooseLanguageDialog = Dialog(requireContext())
        chooseLanguageDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        chooseLanguageDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        chooseLanguageDialog.setContentView(binding.root)
        chooseLanguageDialog.show()

        val chooseLanguageLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        chooseLanguageAdapter = ChooseLanguageAdapter(requireContext()) {
            chooseLanguageDialog.dismiss()
            playEpisode(episode, it)
        }
        binding.rvChooseLanguage.layoutManager = chooseLanguageLayout
        binding.rvChooseLanguage.adapter = chooseLanguageAdapter

        viewModel.availableLanguages.observe(viewLifecycleOwner, {
            val languages = it.reversed()
            chooseLanguageAdapter.setLanguageList(languages)
            binding.rvChooseLanguage.requestFocus()
        })
    }

    private fun playEpisode(episode: Int, chosenLanguage: String) {
        val intent = Intent(context, TvVideoPlayerActivity::class.java).apply {
            putExtra(
                AppConstants.VIDEO_PLAYER_DATA, VideoPlayerData(
                    (parentFragment as TvEpisodesFragment).titleId,
                    true,
                    viewModel.chosenSeason.value!!,
                    chosenLanguage,
                    episode,
                    0L,
                    null
                )
            )
        }
        startActivity(intent)
        if (requireActivity() is TvVideoPlayerActivity) {
            (requireActivity() as TvVideoPlayerActivity).setCurrentFragmentState(TvVideoPlayerActivity.NEW_EPISODE)
        }
        requireActivity().finish()
    }

    override fun onDetach() {
        super.onDetach()
        onTitleSelected = null
        onFirstItem = null
    }
}