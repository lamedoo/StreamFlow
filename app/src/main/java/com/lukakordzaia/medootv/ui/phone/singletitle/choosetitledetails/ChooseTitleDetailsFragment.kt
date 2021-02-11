package com.lukakordzaia.medootv.ui.phone.singletitle.choosetitledetails

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lukakordzaia.medootv.R
import com.lukakordzaia.medootv.helpers.SpinnerClass
import com.lukakordzaia.medootv.ui.phone.singletitle.SingleTitleViewModel
import com.lukakordzaia.medootv.utils.*
import kotlinx.android.synthetic.main.phone_choose_title_details_fragment.*
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
        return inflater.inflate(R.layout.phone_choose_title_details_fragment, container, false)
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
                singleTitleViewModel.onEpisodePressed(args.titleId, args.isTvShow)
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
                        choose_movie_details_continue.text = String.format("გააგრძელეთ: \nს:${it.season} ე:${it.episode} / %02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration),
                                TimeUnit.MILLISECONDS.toSeconds(it.watchedDuration) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration))
                        )
                    } else {
                        choose_movie_details_continue.text = String.format("გააგრძელეთ: \n%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration),
                                TimeUnit.MILLISECONDS.toSeconds(it.watchedDuration) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration))
                        )
                    }
                })

                choose_movie_details_continue.setVisible()
                choose_movie_details_continue.backgroundTintList = ColorStateList.valueOf(requireContext().resources.getColor(R.color.accent_color))
                choose_movie_details_continue.setTextColor(Color.parseColor("#FFFFFF"))

                choose_movie_details_play.text = "თავიდან ყურება"
                choose_movie_details_play.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                choose_movie_details_play.setTextColor(requireContext().resources.getColor(R.color.accent_color))

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