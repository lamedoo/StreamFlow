package com.lukakordzaia.streamflow.ui.phone.singletitle.choosetitledetails

import android.app.Dialog
import android.content.Intent
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.streamflow.databinding.DialogChooseLanguageBinding
import com.lukakordzaia.streamflow.databinding.FragmentPhoneChooseTitleDetailsBinding
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.baseclasses.BaseBottomSheet
import com.lukakordzaia.streamflow.ui.phone.videoplayer.VideoPlayerActivity
import com.lukakordzaia.streamflow.ui.tv.details.titledetails.TvChooseLanguageAdapter
import com.lukakordzaia.streamflow.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ChooseTitleDetailsFragment : BaseBottomSheet<FragmentPhoneChooseTitleDetailsBinding>() {
    private val chooseTitleDetailsViewModel: ChooseTitleDetailsViewModel by viewModel()
    private lateinit var chooseTitleDetailsEpisodesAdapter: ChooseTitleDetailsEpisodesAdapter
    private lateinit var chooseTitleDetailsSeasonAdapter: ChooseTitleDetailsSeasonAdapter
    private lateinit var chooseLanguageAdapter: TvChooseLanguageAdapter
    private val args: ChooseTitleDetailsFragmentArgs by navArgs()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPhoneChooseTitleDetailsBinding
        get() = FragmentPhoneChooseTitleDetailsBinding::inflate

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
        chooseTitleDetailsViewModel.noInternet.observe(viewLifecycleOwner, EventObserver { noInternet ->
            if (noInternet) {
                requireContext().createToast(AppConstants.NO_INTERNET)
                Handler(Looper.getMainLooper()).postDelayed({
                    chooseTitleDetailsViewModel.getSeasonFiles(args.titleId, 1)
                }, 5000)
            }
        })

        chooseTitleDetailsViewModel.chooseDetailsLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> binding.detailsProgressBar.setVisible()
                LoadingState.Status.SUCCESS -> {
                    binding.detailsProgressBar.setGone()

                    if (chooseTitleDetailsViewModel.movieNotYetAdded.value == false) {
                        binding.filesContainer.setVisible()
                    }
                }
            }
        })

        chooseTitleDetailsViewModel.movieNotYetAdded.observe(viewLifecycleOwner, {
            if (it) {
                binding.noFilesContainer.setVisible()
                binding.detailsProgressBar.setGone()
                binding.filesContainer.setGone()
            }
        })

        chooseTitleDetailsViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

        chooseTitleDetailsViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })
    }

    private fun checkAuth() {
        chooseTitleDetailsViewModel.checkAuthDatabase(args.titleId)

        chooseTitleDetailsViewModel.continueWatchingDetails.observe(viewLifecycleOwner, {
            if (it != null) {
                chooseTitleDetailsSeasonAdapter.setChosenSeason(it.season)
                binding.rvSeasons.smoothScrollToPosition(it.season)
                chooseTitleDetailsViewModel.getSeasonFiles(args.titleId, it.season)

                chooseTitleDetailsEpisodesAdapter.setChosenEpisode(it.episode)
            } else {
                chooseTitleDetailsViewModel.getSeasonFiles(args.titleId, 1)
            }
        })
    }

    private fun seasonsContainer() {
        val seasonLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        chooseTitleDetailsSeasonAdapter = ChooseTitleDetailsSeasonAdapter(requireContext()) {
            chooseTitleDetailsViewModel.getSeasonFiles(args.titleId, it)
            chooseTitleDetailsSeasonAdapter.setChosenSeason(it)

            if (it == chooseTitleDetailsViewModel.continueWatchingDetails.value?.season) {
                chooseTitleDetailsEpisodesAdapter.setChosenEpisode(chooseTitleDetailsViewModel.continueWatchingDetails.value!!.episode + 1)
            }
        }
        binding.rvSeasons.layoutManager = seasonLayout
        binding.rvSeasons.adapter = chooseTitleDetailsSeasonAdapter
        ViewCompat.setNestedScrollingEnabled(binding.rvSeasons, false)


        val numOfSeasons = Array(args.numOfSeasons) { i -> (i * 1) + 1 }.toList()
        chooseTitleDetailsSeasonAdapter.setSeasonList(numOfSeasons)
    }

    private fun episodesContainer() {
        chooseTitleDetailsEpisodesAdapter = ChooseTitleDetailsEpisodesAdapter(requireContext(),
            {
                languagePickerDialog(it)
            },
            {
                binding.rvEpisodes.smoothScrollToPosition(it+1)
            })
        binding.rvEpisodes.adapter = chooseTitleDetailsEpisodesAdapter

        chooseTitleDetailsViewModel.episodeInfo.observe(viewLifecycleOwner, {
            chooseTitleDetailsEpisodesAdapter.setEpisodeList(it)
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
        chooseLanguageAdapter = TvChooseLanguageAdapter(requireContext()) { language ->
            chooseLanguageDialog.hide()
            startVideoPlayer(language, episode)
        }
        binding.rvChooseLanguage.layoutManager = chooseLanguageLayout
        binding.rvChooseLanguage.adapter = chooseLanguageAdapter

        chooseTitleDetailsViewModel.availableLanguages.observe(viewLifecycleOwner, { languageList ->
            val languages = languageList.reversed()
            chooseLanguageAdapter.setLanguageList(languages)
        })
    }

    private fun startVideoPlayer(language: String, episode: Int) {
        requireActivity().startActivity(VideoPlayerActivity.startFromSingleTitle(requireContext(), VideoPlayerData(
            args.titleId,
            true,
            chooseTitleDetailsViewModel.chosenSeason.value!!,
            language,
            episode,
            0L,
            null
        )
        ))
    }
}