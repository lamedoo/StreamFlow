package com.lukakordzaia.imoviesapp.ui.videoplayer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.models.MovieFiles
import com.lukakordzaia.imoviesapp.repository.MovieFilesRepository
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class VideoPlayerViewModel : BaseViewModel() {
    private val repository = MovieFilesRepository()

    private val _singleMovieFiles = MutableLiveData<MovieFiles.Data>()
    val singleMovieFiles: LiveData<MovieFiles.Data> = _singleMovieFiles

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