package com.lukakordzaia.imoviesapp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.models.TitleList
import com.lukakordzaia.imoviesapp.repository.HomeRepository
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class HomeViewModel : BaseViewModel() {
    private val repository = HomeRepository()

    private val _movieList = MutableLiveData<List<TitleList.Data>>()
    val movieList: LiveData<List<TitleList.Data>> = _movieList

    private val _tvShowList = MutableLiveData<List<TitleList.Data>>()
    val tvShowList: LiveData<List<TitleList.Data>> = _tvShowList

    fun onSingleTitlePressed(titleId: Int) {
        navigateToNewFragment(HomeFragmentDirections.actionHomeFragmentToSingleTitleFragmentNav(titleId))
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