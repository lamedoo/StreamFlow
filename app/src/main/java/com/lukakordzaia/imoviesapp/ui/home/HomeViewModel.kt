package com.lukakordzaia.imoviesapp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.models.MoviesList
import com.lukakordzaia.imoviesapp.repository.MovieRepository
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class HomeViewModel : BaseViewModel() {
    private val repository = MovieRepository()

    private val _movieList = MutableLiveData<List<MoviesList.Data>>()
    val movieList: LiveData<List<MoviesList.Data>> = _movieList

    private val _tvShowList = MutableLiveData<List<MoviesList.Data>>()
    val tvShowList: LiveData<List<MoviesList.Data>> = _tvShowList

    fun onSingleTitlePressed(movieId: Int) {
        navigateToNewFragment(HomeFragmentDirections.actionHomeFragmentToSingleMovieFragmentNav(movieId))
    }

    fun getMovies() {
        viewModelScope.launch {
            when (val movies = repository.getMovies()) {
                is Result.Success -> {
                    val data = movies.data.data
                    _movieList.value = data
                }
                is Result.Error -> {
                    Log.d("errornewmovies", movies.exception)
                }
            }
        }
    }

    fun getTvShows() {
        viewModelScope.launch {
            when (val tvShows = repository.getTvShows()) {
                is Result.Success -> {
                    val data = tvShows.data.data
                    _tvShowList.value = data
                }
                is Result.Error -> {
                    Log.d("errornewtvshows", tvShows.exception)
                }
            }
        }
    }
}