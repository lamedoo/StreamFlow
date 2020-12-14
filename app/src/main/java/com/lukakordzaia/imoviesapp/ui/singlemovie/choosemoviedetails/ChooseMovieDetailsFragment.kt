package com.lukakordzaia.imoviesapp.ui.singlemovie.choosemoviedetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
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
        viewModel.getSingleMovieFiles(args.movieId)

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
            Log.d("languages", it.toString())
            val languages = it.reversed()
            val adapter = ArrayAdapter(requireContext(),
                    R.layout.spinner_season_item, languages)
            spinner_language.adapter = adapter

            spinner_language.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    viewModel.getLanguageFiles(languages[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        })

        if (!args.isTvShow) {
            tv_season_title.setGone()
            spinner_season_numbers.setGone()
            tv_episode_title.setInvisible()
            spinner_episode_numbers.setGone()
        } else {
            val numOfSeasons = Array(args.numOfSeasons) { i -> (i * 1) + 1 }
            val adapter = ArrayAdapter(requireContext(),
                    R.layout.spinner_season_item, numOfSeasons)
            spinner_season_numbers.adapter = adapter

            spinner_season_numbers.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    viewModel.getSeasonFiles(args.movieId, numOfSeasons[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }

        viewModel.availableEpisodes.observe(viewLifecycleOwner, Observer {
            val numOfEpisodes = Array(it) { i -> (i * 1) + 1 }
            val adapter = ArrayAdapter(requireContext(),
                    R.layout.spinner_season_item, numOfEpisodes)
            spinner_episode_numbers.adapter = adapter

            spinner_episode_numbers.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//                    viewModel.getSeasonFiles(args.movieId, numOfEpisodes[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        })

        viewModel.movieFile.observe(viewLifecycleOwner, Observer {
            choose_movie_details_play.setOnClickListener { _ ->
                viewModel.onPlayButtonPressed(it)
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