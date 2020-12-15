package com.lukakordzaia.imoviesapp.ui.singlemovie.choosemoviedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.utils.*
import kotlinx.android.synthetic.main.fragment_choose_movie_details.*

class ChooseMovieDetailsFragment : BottomSheetDialogFragment() {
    private lateinit var viewModel: ChooseMovieDetailsViewModel
    private val args: ChooseMovieDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_choose_movie_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChooseMovieDetailsViewModel::class.java)
        viewModel.getSingleTitleFiles(args.movieId)
        val spinnerClass = SpinnerClass(requireContext())

        viewModel.movieNotYetAdded.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                movie_file_not_yet.setVisible()
                movie_file_not_yet.text = it
                movie_files_container.setGone()
            } else {
                movie_file_not_yet.setGone()
                movie_files_container.setVisible()
            }
        })

        viewModel.availableLanguages.observe(viewLifecycleOwner, Observer {
            val languages = it.reversed()
            spinnerClass.createSpinner ( spinner_language, languages) { language ->
                viewModel.getTitleLanguageFiles(language)
            }
        })

        if (!args.isTvShow) {
            tv_season_title.setGone()
            spinner_season_numbers.setGone()
            tv_episode_title.setInvisible()
            spinner_episode_numbers.setGone()
        } else {
            val numOfSeasons = Array(args.numOfSeasons) { i -> (i * 1) + 1 }.toList()
            spinnerClass.createSpinner(spinner_season_numbers, numOfSeasons) {
                viewModel.getSeasonFiles(args.movieId, it.toInt())
            }
        }

        viewModel.availableEpisodes.observe(viewLifecycleOwner, Observer { it ->
            val numOfEpisodes = Array(it) { i -> (i * 1) + 1 }.toList()
            spinnerClass.createSpinner(spinner_episode_numbers, numOfEpisodes) { episode ->
                    viewModel.chosenLanguage.observe(viewLifecycleOwner, Observer { language ->
                        viewModel.chosenSeason.observe(viewLifecycleOwner, Observer { season ->
                            viewModel.getEpisodeFile(episode.toInt())
                        })
                    })
            }
        })

        viewModel.movieFile.observe(viewLifecycleOwner, Observer {
            choose_movie_details_play.setOnClickListener { _ ->
                viewModel.onPlayButtonPressed(it, args.movieId, args.isTvShow)
            }
        })

        viewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

        viewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })
    }
}