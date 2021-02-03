package com.lukakordzaia.imoviesapp.ui.phone.singletitle.choosetitledetails

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.helpers.SpinnerClass
import com.lukakordzaia.imoviesapp.ui.phone.singletitle.SingleTitleViewModel
import com.lukakordzaia.imoviesapp.utils.*
import kotlinx.android.synthetic.main.fragment_choose_title_details.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class ChooseTitleDetailsFragment : BottomSheetDialogFragment() {
    private val singleTitleViewModel by viewModel<SingleTitleViewModel>()
    private lateinit var chooseTitleDetailsEpisodesAdapter: ChooseTitleDetailsEpisodesAdapter
    private val args: ChooseTitleDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_choose_title_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        singleTitleViewModel.getSingleTitleFiles(args.titleId)
        val spinnerClass = SpinnerClass(requireContext())

        singleTitleViewModel.isLoading.observe(viewLifecycleOwner, EventObserver {
            if (!it) {
                details_progressBar.setGone()
            }
        })

        singleTitleViewModel.movieNotYetAdded.observe(viewLifecycleOwner, {
            if (it) {
                movie_file_not_yet.setVisible()
                season_spinner_container.setGone()
                rv_episodes.setGone()
            } else {
                movie_files_container.setVisible()
            }
        })

        singleTitleViewModel.availableLanguages.observe(viewLifecycleOwner, {
            val languages = it.reversed()
            spinnerClass.createSpinner(spinner_language, languages) { language ->
                singleTitleViewModel.getTitleLanguageFiles(language)
            }
        })

        if (args.isTvShow) {
            season_spinner_container.setVisible()
            rv_episodes.setVisible()
            choose_movie_details_play.setGone()
        }

        val numOfSeasons = Array(args.numOfSeasons) { i -> (i * 1) + 1 }.toList()
        spinnerClass.createSpinner(spinner_season_numbers, numOfSeasons) {
            singleTitleViewModel.getSeasonFiles(args.titleId, it.toInt())
        }

        singleTitleViewModel.episodeNames.observe(viewLifecycleOwner, {
            chooseTitleDetailsEpisodesAdapter.setEpisodeList(it)
        })

        chooseTitleDetailsEpisodesAdapter = ChooseTitleDetailsEpisodesAdapter(requireContext()) {
            singleTitleViewModel.getEpisodeFile(it)
        }
        rv_episodes.adapter = chooseTitleDetailsEpisodesAdapter

        singleTitleViewModel.chosenEpisode.observe(viewLifecycleOwner, {
            if (it != 0) {
                singleTitleViewModel.onPlayButtonPressed(args.titleId, args.isTvShow)
            }
        })

        singleTitleViewModel.checkTitleInDb(requireContext(), args.titleId).observe(viewLifecycleOwner, {
            singleTitleViewModel.titleIsInDb(it)
        })

        singleTitleViewModel.titleIsInDb.observe(viewLifecycleOwner, { exists ->
            if (exists) {
                singleTitleViewModel.getSingleWatchedTitleDetails(requireContext(), args.titleId).observe(viewLifecycleOwner, {
                    choose_movie_details_continue.setOnClickListener { _ ->
                        singleTitleViewModel.onContinueWatchingPressed(it)
                    }

                    if (args.isTvShow) {
                        choose_movie_details_continue.text = "სეზონი: ${it.season} ეპიზოდი: ${it.episode} / ${String.format("%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(it.watchedTime),
                                TimeUnit.MILLISECONDS.toSeconds(it.watchedTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(it.watchedTime))
                        )} - ${it.language}"
                    } else {
                        choose_movie_details_continue.text = String.format("განაგრძეთ ყურება %02d:%02d - ${it.language}",
                                TimeUnit.MILLISECONDS.toMinutes(it.watchedTime),
                                TimeUnit.MILLISECONDS.toSeconds(it.watchedTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(it.watchedTime))
                        )
                    }
                })

                choose_movie_details_continue.setVisible()
                choose_movie_details_continue.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#009c7c"))
                choose_movie_details_continue.setTextColor(Color.parseColor("#FFFFFF"))

                choose_movie_details_play.text = "თავიდან ყურება"
                choose_movie_details_play.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                choose_movie_details_play.setTextColor(Color.parseColor("#009c7c"))

            }
        })


        choose_movie_details_play.setOnClickListener { _ ->
            singleTitleViewModel.onPlayButtonPressed(args.titleId, args.isTvShow)
        }


        singleTitleViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

        singleTitleViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })
    }
}