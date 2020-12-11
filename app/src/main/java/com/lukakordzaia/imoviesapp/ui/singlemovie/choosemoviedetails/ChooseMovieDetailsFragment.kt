package com.lukakordzaia.imoviesapp.ui.singlemovie.choosemoviedetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lukakordzaia.imoviesapp.R
import com.lukakordzaia.imoviesapp.utils.EventObserver
import com.lukakordzaia.imoviesapp.utils.navController
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

        viewModel.singleMovieFiles.observe(viewLifecycleOwner, Observer { movieFiles ->
            movieFiles.files.forEach { files ->
                when (files.lang) {
                    "GEO" -> {
                        files.files.forEach {
                            if (it.quality == "HIGH") {
                                choose_movie_details_geo.setOnClickListener { _ ->
                                    viewModel.onLanguageChosenPressed(it.src)
                                }
                            }
                        }
                    }
                    "ENG" -> {
                        files.files.forEach {
                            if (it.quality == "HIGH") {
                                choose_movie_details_eng.setOnClickListener { _ ->
                                    viewModel.onLanguageChosenPressed(it.src)
                                }
                            }
                        }
                    }
                    "RUS" -> {
                        files.files.forEach {
                            if (it.quality == "HIGH") {
                                choose_movie_details_rus.setOnClickListener { _ ->
                                    viewModel.onLanguageChosenPressed(it.src)
                                }
                            }
                        }
                    }
            }
            }
        })

        viewModel.navigateScreen.observe(viewLifecycleOwner, EventObserver {
            navController(it)
        })
    }
}