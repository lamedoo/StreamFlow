package com.lukakordzaia.streamflowphone.ui.phone.phonesingletitle.tvshowdetailsbottomsheet

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lukakordzaia.core.adapters.ChooseLanguageAdapter
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.core.datamodels.VideoPlayerData
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.utils.setGone
import com.lukakordzaia.core.utils.setVisible
import com.lukakordzaia.core.utils.setVisibleOrGone
import com.lukakordzaia.streamflowphone.databinding.DialogChooseLanguageBinding
import com.lukakordzaia.streamflowphone.databinding.FragmentPhoneTvShowBottomSheetBinding
import com.lukakordzaia.streamflowphone.ui.baseclasses.BaseBottomSheetVM
import com.lukakordzaia.streamflowphone.ui.phone.videoplayer.VideoPlayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class TvShowBottomSheetFragment : BaseBottomSheetVM<FragmentPhoneTvShowBottomSheetBinding, TvShowBottomSheetViewModel>() {
    private val args: TvShowBottomSheetFragmentArgs by navArgs()
    override val viewModel by viewModel<TvShowBottomSheetViewModel>()
    override val reload: () -> Unit = { viewModel.getSeasonFiles(args.titleId, 1) }

    private lateinit var episodeAdapter: TvShowBottomSheetEpisodesAdapter
    private lateinit var seasonAdapter: TvShowBottomSheetSeasonAdapter
    private lateinit var chooseLanguageAdapter: ChooseLanguageAdapter

    private var scrolledOnce = false

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneTvShowBottomSheetBinding
        get() = FragmentPhoneTvShowBottomSheetBinding::inflate

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener { bottom ->
            val bottomSheet =
                (bottom as BottomSheetDialog).findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.chooseDetailsTitle.text = args.titleName

        viewModel.checkAuthDatabase(args.titleId)

        fragmentSetUi()
        fragmentListeners()
        fragmentObservers()
    }

    private fun fragmentSetUi() {
        seasonsContainer()
        episodesContainer()
    }

    private fun fragmentListeners() {
        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun fragmentObservers() {
        viewModel.generalLoader.observe(viewLifecycleOwner, {
            binding.detailsProgressBar.setVisibleOrGone(it == LoadingState.LOADING)
        })

        viewModel.movieNotYetAdded.observe(viewLifecycleOwner, {
            if (it) {
                binding.noFilesContainer.setVisible()
                binding.detailsProgressBar.setGone()
                binding.filesContainer.setGone()
            }
        })

        viewModel.continueWatchingDetails.observe(viewLifecycleOwner, {
            checkContinueWatching(it)
        })

        viewModel.episodeInfo.observe(viewLifecycleOwner, {
            episodeAdapter.setEpisodeList(it)
        })

        viewModel.availableLanguages.observe(viewLifecycleOwner, { languageList ->
            val languages = languageList.reversed()
            chooseLanguageAdapter.setLanguageList(languages)
        })
    }

    private fun checkContinueWatching(info: ContinueWatchingRoom?) {
            if (info != null) {
                seasonAdapter.setChosenSeason(info.season)
                viewModel.getSeasonFiles(args.titleId, info.season)

                episodeAdapter.setChosenEpisode(info.episode+1)
            } else {
                viewModel.getSeasonFiles(args.titleId, 1)
            }
    }

    private fun seasonsContainer() {
        val seasonLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        seasonAdapter = TvShowBottomSheetSeasonAdapter(requireContext(),
            {
                viewModel.getSeasonFiles(args.titleId, it)
                seasonAdapter.setChosenSeason(it)
                scrolledOnce = false

                if (it == viewModel.continueWatchingDetails.value?.season) {
                    episodeAdapter.setChosenEpisode(viewModel.continueWatchingDetails.value!!.episode + 1)
                } else {
                    episodeAdapter.setChosenEpisode(-1)
                }
            },
            {
                binding.rvSeasons.smoothScrollToPosition(it)
            })
        binding.rvSeasons.apply {
            adapter = seasonAdapter
            layoutManager = seasonLayout
            ViewCompat.setNestedScrollingEnabled(this, false)
        }

        val numOfSeasons = Array(args.numOfSeasons) { i -> (i * 1) + 1 }.toList()
        seasonAdapter.setSeasonList(numOfSeasons)
    }

    private fun episodesContainer() {
        episodeAdapter = TvShowBottomSheetEpisodesAdapter(requireContext(),
            {
                languagePickerDialog(it)
            },
            {
                if (!scrolledOnce) {
                    binding.rvEpisodes.smoothScrollToPosition(it+1)
                    scrolledOnce = true
                }
            })
        binding.rvEpisodes.adapter = episodeAdapter
    }

    private fun languagePickerDialog(episode: Int) {
        viewModel.getEpisodeLanguages(args.titleId, episode)

        val binding = DialogChooseLanguageBinding.inflate(LayoutInflater.from(requireContext()))
        val chooseLanguageDialog = Dialog(requireContext())
        chooseLanguageDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        chooseLanguageDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        chooseLanguageDialog.setContentView(binding.root)
        chooseLanguageDialog.show()

        val chooseLanguageLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        chooseLanguageAdapter = ChooseLanguageAdapter(requireContext()) { language ->
            chooseLanguageDialog.hide()
            startVideoPlayer(language, episode)
        }
        binding.rvChooseLanguage.apply {
            adapter = chooseLanguageAdapter
            layoutManager = chooseLanguageLayout
        }
    }

    private fun startVideoPlayer(language: String, episode: Int) {
        requireActivity().startActivity(VideoPlayerActivity.startFromSingleTitle(requireContext(), VideoPlayerData(
            args.titleId,
            true,
            viewModel.chosenSeason.value!!,
            language,
            episode,
            0L,
            null
        )
        ))
    }
}