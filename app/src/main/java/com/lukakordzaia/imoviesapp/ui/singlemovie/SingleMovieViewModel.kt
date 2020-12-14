package com.lukakordzaia.imoviesapp.ui.singlemovie

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.models.MovieData
import com.lukakordzaia.imoviesapp.network.models.MovieDetails
import com.lukakordzaia.imoviesapp.repository.SingleMovieRepository
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class SingleMovieViewModel : BaseViewModel() {
    private val repository = SingleMovieRepository()

    private val _singleMovieFiles = MutableLiveData<MovieData.Data>()
    val singleMovieFiles: LiveData<MovieData.Data> = _singleMovieFiles

    private val _numOfSeasons = MutableLiveData<Pair<Int, Boolean>>()
    val numOfSeasons: LiveData<Pair<Int, Boolean>> = _numOfSeasons

    private val _movieDetails = MutableLiveData<MovieDetails>()
    val movieDetails: LiveData<MovieDetails> = _movieDetails



    fun onPlayPressed(movieId: Int, movieDetails: MovieDetails) {
        navigateToNewFragment(SingleMovieFragmentDirections.actionSingleMovieFragmentToChooseMovieDetailsFragment(
                movieId,
                movieDetails.numOfSeasons,
                movieDetails.isTvShow,
                arrayOf("ENG", "RUS")
        ))
    }

    fun getSingleMovieFiles(movieId: Int) {
        viewModelScope.launch {
            when (val data = repository.getSingleMovieData(movieId)) {
                is Result.Success -> {
                    _singleMovieFiles.value = data.data.data
                    checkTvShowAndFiles()
                }
                is Result.Error -> {
                    Log.d("error", data.exception)
                }
            }
        }
    }

    fun checkTvShowAndFiles() {
        if (singleMovieFiles.value!!.isTvShow != false) {
            if (!singleMovieFiles.value!!.seasons!!.data.isNullOrEmpty()) {
                _numOfSeasons.value = singleMovieFiles.value!!.seasons!!.data?.last()?.number!! to true
                _movieDetails.value = MovieDetails(singleMovieFiles.value!!.seasons!!.data?.last()?.number!!, true)

            } else {
                _numOfSeasons.value = 0 to true
                _movieDetails.value = MovieDetails(0, true)
            }
        } else {
            _numOfSeasons.value = 0 to false
            _movieDetails.value = MovieDetails(0, false)
        }
    }
}