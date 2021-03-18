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
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.ui.phone.videoplayer.VideoPlayerActivity
import com.lukakordzaia.streamflow.ui.tv.details.titledetails.TvChooseLanguageAdapter
import com.lukakordzaia.streamflow.utils.*
import kotlinx.android.synthetic.main.phone_choose_title_details_fragment.*
import kotlinx.android.synthetic.main.tv_choose_language_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ChooseTitleDetailsFragment : BottomSheetDialogFragment() {
    private val chooseTitleDetailsViewModel: ChooseTitleDetailsViewModel by viewModel()
    private lateinit var chooseTitleDetailsEpisodesAdapter: ChooseTitleDetailsEpisodesAdapter
    private lateinit var chooseTitleDetailsSeasonAdapter: ChooseTitleDetailsSeasonAdapter
    private lateinit var chooseLanguageAdapter: TvChooseLanguageAdapter
    private val args: ChooseTitleDetailsFragmentArgs by navArgs()

//    private var chosenSeasonInAdapter = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View? {
        return inflater.inflate(R.layout.phone_choose_title_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chooseTitleDetailsViewModel.getSeasonFiles(args.titleId, 1)

        val seasonLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        chooseTitleDetailsSeasonAdapter = ChooseTitleDetailsSeasonAdapter(requireContext(),
            {
                chooseTitleDetailsViewModel.getSeasonFiles(args.titleId, it)
                chooseTitleDetailsSeasonAdapter.setChosenSeason(it)

                if (it == chooseTitleDetailsViewModel.continueWatchingDetails.value?.season) {
                    chooseTitleDetailsEpisodesAdapter.setChosenEpisode(chooseTitleDetailsViewModel.continueWatchingDetails.value!!.episode+1)
                } else {
                    chooseTitleDetailsEpisodesAdapter.setChosenEpisode(-1)
                    rv_episodes.smoothScrollToPosition(0)
                }
            },
            {

            })
        rv_seasons.layoutManager = seasonLayout
        rv_seasons.adapter = chooseTitleDetailsSeasonAdapter
        ViewCompat.setNestedScrollingEnabled(rv_seasons, false)

        chooseTitleDetailsViewModel.noInternet.observe(viewLifecycleOwner, EventObserver { noInternet ->
                if (noInternet) {
                    requireContext().createToast(AppConstants.NO_INTERNET)
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (args.isTvShow) {
                            chooseTitleDetailsViewModel.getSeasonFiles(args.titleId, 1)
                        } else {
                            chooseTitleDetailsViewModel.getSeasonFiles(args.titleId, args.numOfSeasons)
                        }
                    }, 5000)
                }
            })

        chooseTitleDetailsViewModel.chooseDetailsLoader.observe(viewLifecycleOwner, {
            when (it.status) {
                LoadingState.Status.RUNNING -> details_progressBar.setVisible()
                LoadingState.Status.SUCCESS -> {
                    details_progressBar.setGone()

                    if (chooseTitleDetailsViewModel.movieNotYetAdded.value == false) {
                        movie_files_container.setVisible()
                    }
                }
            }
        })

        chooseTitleDetailsViewModel.movieNotYetAdded.observe(viewLifecycleOwner, {
            if (it) {
                title_file_not_yet.setVisible()
                details_progressBar.setGone()
                movie_files_container.setGone()
            }
        })

        if (args.isTvShow) {
            rv_seasons.setVisible()
            rv_episodes.setVisible()
            val numOfSeasons = Array(args.numOfSeasons) { i -> (i * 1) + 1 }.toList()
            chooseTitleDetailsSeasonAdapter.setSeasonList(numOfSeasons)
        } else {
            chooseTitleDetailsViewModel.getSeasonFiles(args.titleId, args.numOfSeasons)
        }

        chooseTitleDetailsViewModel.episodeInfo.observe(viewLifecycleOwner, {
            chooseTitleDetailsEpisodesAdapter.setEpisodeList(it)
        })

        chooseTitleDetailsEpisodesAdapter = ChooseTitleDetailsEpisodesAdapter(requireContext())
        {
            languagePickerDialog(it)
        }
        rv_episodes.adapter = chooseTitleDetailsEpisodesAdapter

        if (Firebase.auth.currentUser == null) {
            chooseTitleDetailsViewModel.checkContinueWatchingTitleInRoom(requireContext(), args.titleId).observe(viewLifecycleOwner, { exists ->
                if (exists) {
                    chooseTitleDetailsViewModel.getSingleContinueWatchingFromRoom(requireContext(), args.titleId)
                }
            })
        } else {
            chooseTitleDetailsViewModel.checkContinueWatchingInFirestore(args.titleId)
        }

        chooseTitleDetailsViewModel.continueWatchingDetails.observe(viewLifecycleOwner, {
            if (it != null) {

                chooseTitleDetailsSeasonAdapter.setChosenSeason(it.season)
                rv_seasons.smoothScrollToPosition(it.season)
                chooseTitleDetailsViewModel.getSeasonFiles(args.titleId, it.season)

                chooseTitleDetailsEpisodesAdapter.setChosenEpisode(it.episode)
                rv_episodes.smoothScrollToPosition(it.episode+1)

            }
        })

        choose_details_title.text = args.titleName

        choose_details_close.setOnClickListener {
            dismiss()
        }


        chooseTitleDetailsViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

        chooseTitleDetailsViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })
    }

    private fun languagePickerDialog(episode: Int) {
        val chooseLanguageDialog = Dialog(requireContext())
        chooseLanguageDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        chooseLanguageDialog.setContentView(layoutInflater.inflate(R.layout.tv_choose_language_dialog, null))
        chooseLanguageDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        chooseLanguageDialog.show()

        val chooseLanguageLayout = GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        chooseLanguageAdapter = TvChooseLanguageAdapter(requireContext()) { language ->
            chooseLanguageDialog.hide()
//            chooseTitleDetailsViewModel.onEpisodePressed(args.titleId, args.isTvShow, episode, language)
            val intent = Intent(context, VideoPlayerActivity::class.java)
            intent.putExtra("videoPlayerData", VideoPlayerData(
                args.titleId,
                args.isTvShow,
                chooseTitleDetailsViewModel.chosenSeason.value!!,
                language,
                episode,
                0L,
                null
            )
            )
            activity?.startActivity(intent)
        }
        chooseLanguageDialog.rv_tv_choose_language.layoutManager = chooseLanguageLayout
        chooseLanguageDialog.rv_tv_choose_language.adapter = chooseLanguageAdapter

        chooseTitleDetailsViewModel.availableLanguages.observe(viewLifecycleOwner, { languageList ->
            val languages = languageList.reversed()
            chooseLanguageAdapter.setLanguageList(languages)
        })
    }
}