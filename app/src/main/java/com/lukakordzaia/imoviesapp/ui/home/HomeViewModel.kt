package com.lukakordzaia.imoviesapp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.models.MovieData
import com.lukakordzaia.imoviesapp.network.models.MoviesList
import com.lukakordzaia.imoviesapp.repository.MovieRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repository = MovieRepository()

    private val _movieList = MutableLiveData<List<MoviesList.Data>>()

    val movieList: LiveData<List<MoviesList.Data>> = _movieList

    fun getMovies() {
        viewModelScope.launch {
            when (val movies = repository.getMovies()) {
                is Result.Success -> {
                    val data = movies.data.data
                    _movieList.value = data
                }
                is Result.Error -> {
                    Log.d("error", movies.exception)
                }
            }
        }
    }
}