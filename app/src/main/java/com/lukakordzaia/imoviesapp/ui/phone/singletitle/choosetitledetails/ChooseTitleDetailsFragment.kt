package com.lukakordzaia.imoviesapp.ui.phone.singletitle.choosetitledetails

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.helpers.SpinnerClass
import com.lukakordzaia.imoviesapp.ui.phone.singletitle.SingleTitleViewModel
import com.lukakordzaia.imoviesapp.utils.*
import kotlinx.android.synthetic.main.fragment_choose_title_details.*
import java.util.concurrent.TimeUnit


class ChooseTitleDetailsFragment : BottomSheetDialogFragment() {
    private lateinit var chooseTitleDetailsViewModel: ChooseTitleDetailsViewModel
    private lateinit var singleTitleViewModel: SingleTitleViewModel
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
        chooseTitleDetailsViewModel = ViewModelProvider(this).get(ChooseTitleDetailsViewModel::class.java)
        singleTitleViewModel = ViewModelProvider(this).get(SingleTitleViewModel::class.java)

        chooseTitleDetailsViewModel.getSingleTitleFiles(args.titleId)
        val spinnerClass = SpinnerClass(requireContext())

        chooseTitleDetailsViewModel.isLoading.observe(viewLifecycleOwner, EventObserver {
            if (!it) {
                details_progressBar.setGone()
            }
        })

        chooseTitleDetailsViewModel.movieNotYetAdded.observe(viewLifecycleOwner, {
            if (it) {
                movie_file_not_yet.setVisible()
                season_spinner_container.setGone()
                rv_episodes.setGone()
            } else {
                movie_files_container.setVisible()
            }
        })

        chooseTitleDetailsViewModel.availableLanguages.observe(viewLifecycleOwner, {
            val languages = it.reversed()
            spinnerClass.createSpinner(spinner_language, languages) { language ->
                chooseTitleDetailsViewModel.getTitleLanguageFiles(language)
            }
        })

        if (args.isTvShow) {
            season_spinner_container.setVisible()
            rv_episodes.setVisible()
            choose_movie_details_play.setGone()
        }

        val numOfSeasons = Array(args.numOfSeasons) { i -> (i * 1) + 1 }.toList()
        spinnerClass.createSpinner(spinner_season_numbers, numOfSeasons) {
            chooseTitleDetailsViewModel.getSeasonFiles(args.titleId, it.toInt())
        }

        chooseTitleDetailsViewModel.episodeNames.observe(viewLifecycleOwner, {
            chooseTitleDetailsEpisodesAdapter.setEpisodeList(it)
        })

        chooseTitleDetailsEpisodesAdapter = ChooseTitleDetailsEpisodesAdapter(requireContext()) {
            chooseTitleDetailsViewModel.getEpisodeFile(it)
        }
        rv_episodes.adapter = chooseTitleDetailsEpisodesAdapter

        chooseTitleDetailsViewModel.chosenEpisode.observe(viewLifecycleOwner, {
            if (it != 0) {
                chooseTitleDetailsViewModel.onPlayButtonPressed(args.titleId, args.isTvShow)
            }
        })

        singleTitleViewModel.checkTitleInDb(requireContext(), args.titleId).observe(viewLifecycleOwner, {
            singleTitleViewModel.titleIsInDb(it)
        })

        singleTitleViewModel.titleIsInDb.observe(viewLifecycleOwner, { exists ->
            if (exists) {
                singleTitleViewModel.getSingleWatchedTitleDetails(requireContext(), args.titleId).observe(viewLifecycleOwner, {
                    choose_movie_details_continue.setOnClickListener { _ ->
                        chooseTitleDetailsViewModel.onContinueWatchingPressed(it)
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
            chooseTitleDetailsViewModel.onPlayButtonPressed(args.titleId, args.isTvShow)
        }


        chooseTitleDetailsViewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

        chooseTitleDetailsViewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })
    }
}