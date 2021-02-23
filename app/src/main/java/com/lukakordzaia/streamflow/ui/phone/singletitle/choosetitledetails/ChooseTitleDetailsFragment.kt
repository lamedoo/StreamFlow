package com.lukakordzaia.streamflow.ui.phone.singletitle.choosetitledetails

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lukakordzaia.streamflow.R
import com.lukakordzaia.streamflow.helpers.SpinnerClass
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.utils.*
import kotlinx.android.synthetic.main.phone_choose_title_details_fragment.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class ChooseTitleDetailsFragment : BottomSheetDialogFragment() {
    private val chooseTitleDetailsViewModel by viewModel<ChooseTitleDetailsViewModel>()
    private val spinnerClass: SpinnerClass by inject()
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

        chooseTitleDetailsViewModel.noInternet.observe(viewLifecycleOwner, EventObserver { noInternet ->
            if (noInternet) {
                requireContext().createToast(AppConstants.NO_INTERNET)
                Handler(Looper.getMainLooper()).postDelayed({
                    if (args.isTvShow) {
                        val numOfSeasons = Array(args.numOfSeasons) { i -> (i * 1) + 1 }.toList()
                        spinnerClass.createSpinner(spinner_season_numbers, numOfSeasons) {
                            chooseTitleDetailsViewModel.getSeasonFiles(args.titleId, it.toInt())
                        }
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
            season_spinner_container.setVisible()
            rv_episodes.setVisible()
            val numOfSeasons = Array(args.numOfSeasons) { i -> (i * 1) + 1 }.toList()
            spinnerClass.createSpinner(spinner_season_numbers, numOfSeasons) {
                chooseTitleDetailsViewModel.getSeasonFiles(args.titleId, it.toInt())
            }
        } else {
            chooseTitleDetailsViewModel.getSeasonFiles(args.titleId, args.numOfSeasons)
        }

        chooseTitleDetailsViewModel.availableLanguages.observe(viewLifecycleOwner, {
                    val languages = it.reversed()
                    spinnerClass.createSpinner(spinner_language, languages) { language ->
                        chooseTitleDetailsViewModel.setFileLanguage(language)
                    }
                })

        chooseTitleDetailsViewModel.episodeNames.observe(viewLifecycleOwner, {
                    chooseTitleDetailsEpisodesAdapter.setEpisodeList(it)
                })

        chooseTitleDetailsEpisodesAdapter = ChooseTitleDetailsEpisodesAdapter(requireContext())
        {
            chooseTitleDetailsViewModel.onEpisodePressed(args.titleId, args.isTvShow, it)
        }
        rv_episodes.adapter = chooseTitleDetailsEpisodesAdapter

        if (Firebase.auth.currentUser == null) {
            chooseTitleDetailsViewModel.checkTitleInDb(requireContext(), args.titleId).observe(viewLifecycleOwner, { exists ->
                if (exists) {
                    chooseTitleDetailsViewModel.getTitleDbDetails(requireContext(), args.titleId)
//                    chooseTitleDetailsViewModel.continueWatchingDetails.observe(viewLifecycleOwner, {
//                        choose_movie_details_continue.setOnClickListener { _ ->
//                            chooseTitleDetailsViewModel.onContinueWatchingPressed(it)
//                        }
//
//                        if (args.isTvShow) {
//                            choose_movie_details_continue.text = String.format("გააგრძელეთ: \nს:${it.season} ე:${it.episode} / %02d:%02d",
//                                    TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration),
//                                    TimeUnit.MILLISECONDS.toSeconds(it.watchedDuration) -
//                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration))
//                            )
//                        } else {
//                            choose_movie_details_continue.text = String.format("გააგრძელეთ: \n%02d:%02d",
//                                    TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration),
//                                    TimeUnit.MILLISECONDS.toSeconds(it.watchedDuration) -
//                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration))
//                            )
//                        }
//                    })
//
//                    choose_movie_details_continue.setVisible()
//                    choose_movie_details_continue.backgroundTintList = ColorStateList.valueOf(requireContext().resources.getColor(R.color.accent_color))
//                    choose_movie_details_continue.setTextColor(Color.parseColor("#FFFFFF"))
//
//                    choose_movie_details_play.text = "თავიდან ყურება"
//                    choose_movie_details_play.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
//                    choose_movie_details_play.setTextColor(requireContext().resources.getColor(R.color.accent_color))

                }
            })
        } else {
            chooseTitleDetailsViewModel.checkContinueWatchingInFirestore(args.titleId)
//            chooseTitleDetailsViewModel.continueWatchingDetails.observe(viewLifecycleOwner, {
//                if (it != null) {
//                    choose_movie_details_continue.setOnClickListener { _ ->
//                        chooseTitleDetailsViewModel.onContinueWatchingPressed(it)
//                    }
//
//                    if (args.isTvShow) {
//                        choose_movie_details_continue.text = String.format("გააგრძელეთ: \nს:${it.season} ე:${it.episode} / %02d:%02d",
//                                TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration),
//                                TimeUnit.MILLISECONDS.toSeconds(it.watchedDuration) -
//                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration))
//                        )
//                    } else {
//                        choose_movie_details_continue.text = String.format("გააგრძელეთ: \n%02d:%02d",
//                                TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration),
//                                TimeUnit.MILLISECONDS.toSeconds(it.watchedDuration) -
//                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(it.watchedDuration))
//                        )
//                    }
//                    choose_movie_details_continue.setVisible()
//                    choose_movie_details_continue.backgroundTintList = ColorStateList.valueOf(requireContext().resources.getColor(R.color.accent_color))
//                    choose_movie_details_continue.setTextColor(Color.parseColor("#FFFFFF"))
//
//                    choose_movie_details_play.text = "თავიდან ყურება"
//                    choose_movie_details_play.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
//                    choose_movie_details_play.setTextColor(requireContext().resources.getColor(R.color.accent_color))
//                }
//            })
        }

        chooseTitleDetailsViewModel.continueWatchingDetails.observe(viewLifecycleOwner, {
            if (it != null) {
                choose_movie_details_continue.setOnClickListener { _ ->
                    chooseTitleDetailsViewModel.onContinueWatchingPressed(it)
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
                choose_movie_details_continue.setVisible()
                choose_movie_details_continue.backgroundTintList = ColorStateList.valueOf(requireContext().resources.getColor(R.color.accent_color))
                choose_movie_details_continue.setTextColor(Color.parseColor("#FFFFFF"))

                choose_movie_details_play.text = "თავიდან ყურება"
                choose_movie_details_play.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                choose_movie_details_play.setTextColor(requireContext().resources.getColor(R.color.accent_color))
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