package com.lukakordzaia.imoviesapp.ui.singlemovie

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.models.MovieData
import com.lukakordzaia.imoviesapp.repository.SingleMovieRepository
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class SingleMovieViewModel : BaseViewModel() {
    private val repository = SingleMovieRepository()

    private val _singleMovieFiles = MutableLiveData<MovieData.Data>()
    val singleMovieFiles: LiveData<MovieData.Data> = _singleMovieFiles

    fun onPlayPressed(movieId: Int) {
        navigateToNewFragment(SingleMovieFragmentDirections.actionSingleMovieFragmentToChooseMovieDetailsFragment(movieId))
    }

    fun getSingleMovieFiles(movieId: Int) {
        viewModelScope.launch {
            when (val data = repository.getSingleMovieData(movieId)) {
                is Result.Success -> {
                    _singleMovieFiles.value = data.data.data
                }
                is Result.Error -> {
                    Log.d("error", data.exception)
                }
            }
        }
    }
}