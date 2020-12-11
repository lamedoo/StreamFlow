package com.lukakordzaia.imoviesapp.ui.singlemovie.choosemoviedetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.models.MovieFiles
import com.lukakordzaia.imoviesapp.repository.VideoPlayerRepository
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class ChooseMovieDetailsViewModel : BaseViewModel() {
    private val repository = VideoPlayerRepository()

    private val _singleMovieFiles = MutableLiveData<MovieFiles.Data>()
    val singleMovieFiles: LiveData<MovieFiles.Data> = _singleMovieFiles

    fun onLanguageChosenPressed(mediaLink: String) {
        navigateToNewFragment(ChooseMovieDetailsFragmentDirections.actionChooseMovieDetailsFragmentToVideoPlayerFragment(mediaLink))
    }

    fun getSingleMovieFiles(movieId: Int) {
        viewModelScope.launch {
            when (val files = repository.getSingleMovieFiles(movieId)) {
                is Result.Success -> {
                    _singleMovieFiles.value = files.data.data[0]
                }
                is Result.Error -> {
                    Log.d("error", files.exception)
                }
            }
        }
    }
}