package com.lukakordzaia.imoviesapp.ui.singletitle.choosetitledetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.utils.*
import kotlinx.android.synthetic.main.fragment_choose_title_details.*


class ChooseTitleDetailsFragment : BottomSheetDialogFragment() {
    private lateinit var viewModel: ChooseTitleDetailsViewModel
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
        viewModel = ViewModelProvider(this).get(ChooseTitleDetailsViewModel::class.java)
        viewModel.getSingleTitleFiles(args.titleId)
        val spinnerClass = SpinnerClass(requireContext())

        viewModel.movieNotYetAdded.observe(viewLifecycleOwner, {
            if (!it) {
                movie_file_not_yet.setGone()
                movie_files_container.setVisible()
            }
        })

        viewModel.availableLanguages.observe(viewLifecycleOwner, {
            val languages = it.reversed()
            spinnerClass.createSpinner(spinner_language, languages) { language ->
                viewModel.getTitleLanguageFiles(language)
            }
        })

        if (!args.isTvShow) {
            tv_season_title.setGone()
            spinner_season_numbers.setGone()
            tv_episode_title.setGone()
            spinner_episode_numbers.setGone()
        } else {
            val numOfSeasons = Array(args.numOfSeasons) { i -> (i * 1) + 1 }.toList()
            spinnerClass.createSpinner(spinner_season_numbers, numOfSeasons) {
                viewModel.getSeasonFiles(args.titleId, it.toInt())
            }
        }

        viewModel.availableEpisodes.observe(viewLifecycleOwner, { it ->
            val numOfEpisodes = Array(it) { i -> (i * 1) + 1 }.toList()
            spinnerClass.createSpinner(spinner_episode_numbers, numOfEpisodes) { episode ->
                viewModel.getEpisodeFile(episode.toInt())
            }
        })


            choose_movie_details_play.setOnClickListener { _ ->
                viewModel.onPlayButtonPressed(args.titleId, args.isTvShow)
            }


        viewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })

        viewModel.toastMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().createToast(it)
        })
    }
}