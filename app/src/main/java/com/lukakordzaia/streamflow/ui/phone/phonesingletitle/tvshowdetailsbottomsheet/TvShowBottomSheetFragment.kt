package com.lukakordzaia.streamflow.ui.phone.phonesingletitle.tvshowdetailsbottomsheet

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lukakordzaia.streamflow.databinding.DialogChooseLanguageBinding
import com.lukakordzaia.streamflow.databinding.FragmentPhoneTvShowBottomSheetBinding
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseBottomSheetVM
import com.lukakordzaia.streamflow.ui.phone.videoplayer.VideoPlayerActivity
import com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitledetails.ChooseLanguageAdapter
import com.lukakordzaia.streamflow.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class TvShowBottomSheetFragment : BaseBottomSheetVM<FragmentPhoneTvShowBottomSheetBinding, TvShowBottomSheetViewModel>() {
    override val viewModel by viewModel<TvShowBottomSheetViewModel>()

    private lateinit var tvShowBottomSheetEpisodesAdapter: TvShowBottomSheetEpisodesAdapter
    private lateinit var tvShowBottomSheetSeasonAdapter: TvShowBottomSheetSeasonAdapter
    private lateinit var chooseLanguageAdapter: ChooseLanguageAdapter
    private val args: TvShowBottomSheetFragmentArgs by navArgs()

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

        viewModel.getSeasonFiles(args.titleId, 1)

        fragmentListeners()
        fragmentObservers()
        checkAuth()
        seasonsContainer()
        episodesContainer()
    }

    private fun fragmentListeners() {
        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun fragmentObservers() {
        viewModel.noInternet.observe(viewLifecycleOwner, EventObserver { noInternet ->
            if (noInternet) {
                requireContext().createToast(AppConstants.NO_INTERNET)
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.getSeasonFiles(args.titleId, 1)
                }, 5000)
            }
        })

        viewModel.generalLoader.observe(viewLifecycleOwner, {
            when (it) {
                LoadingState.LOADING -> binding.detailsProgressBar.setVisible()
                LoadingState.LOADED -> {
                    binding.detailsProgressBar.setGone()

                    if (viewModel.movieNotYetAdded.value == false) {
                        binding.filesContainer.setVisible()
                    }
                }
            }
        })

        viewModel.movieNotYetAdded.observe(viewLifecycleOwner, {
            if (it) {
                binding.noFilesContainer.setVisible()
                binding.detailsProgressBar.setGone()
                binding.filesContainer.setGone()
            }
        })
    }

    private fun checkAuth() {
        viewModel.checkAuthDatabase(args.titleId)

        viewModel.continueWatchingDetails.observe(viewLifecycleOwner, {
            if (it != null) {
                tvShowBottomSheetSeasonAdapter.setChosenSeason(it.season)
                binding.rvSeasons.smoothScrollToPosition(it.season)
                viewModel.getSeasonFiles(args.titleId, it.season)

                tvShowBottomSheetEpisodesAdapter.setChosenEpisode(it.episode)
            } else {
                viewModel.getSeasonFiles(args.titleId, 1)
            }
        })
    }

    private fun seasonsContainer() {
        val seasonLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        tvShowBottomSheetSeasonAdapter = TvShowBottomSheetSeasonAdapter(requireContext()) {
            viewModel.getSeasonFiles(args.titleId, it)
            tvShowBottomSheetSeasonAdapter.setChosenSeason(it)

            if (it == viewModel.continueWatchingDetails.value?.season) {
                tvShowBottomSheetEpisodesAdapter.setChosenEpisode(viewModel.continueWatchingDetails.value!!.episode + 1)
            }
        }
        binding.rvSeasons.layoutManager = seasonLayout
        binding.rvSeasons.adapter = tvShowBottomSheetSeasonAdapter
        ViewCompat.setNestedScrollingEnabled(binding.rvSeasons, false)


        val numOfSeasons = Array(args.numOfSeasons) { i -> (i * 1) + 1 }.toList()
        tvShowBottomSheetSeasonAdapter.setSeasonList(numOfSeasons)
    }

    private fun episodesContainer() {
        tvShowBottomSheetEpisodesAdapter = TvShowBottomSheetEpisodesAdapter(requireContext(),
            {
                languagePickerDialog(it)
            },
            {
                binding.rvEpisodes.smoothScrollToPosition(it+1)
            })
        binding.rvEpisodes.adapter = tvShowBottomSheetEpisodesAdapter

        viewModel.episodeInfo.observe(viewLifecycleOwner, {
            tvShowBottomSheetEpisodesAdapter.setEpisodeList(it)
        })
    }

    private fun languagePickerDialog(episode: Int) {
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
        binding.rvChooseLanguage.layoutManager = chooseLanguageLayout
        binding.rvChooseLanguage.adapter = chooseLanguageAdapter

        viewModel.availableLanguages.observe(viewLifecycleOwner, { languageList ->
            val languages = languageList.reversed()
            chooseLanguageAdapter.setLanguageList(languages)
        })
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