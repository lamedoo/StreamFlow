package com.lukakordzaia.streamflow.ui.tv.categories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.repository.HomeRepository
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class TvCategoriesViewModel(private val repository: HomeRepository) : BaseViewModel() {
    private val _newMovieList = MutableLiveData<List<TitleList.Data>>()
    val newMovieList: LiveData<List<TitleList.Data>> = _newMovieList

    private val _topMovieList = MutableLiveData<List<TitleList.Data>>()
    val topMovieList: LiveData<List<TitleList.Data>> = _topMovieList

    private val _tvShowList = MutableLiveData<List<TitleList.Data>>()
    val tvShowList: LiveData<List<TitleList.Data>> = _tvShowList

    fun getNewMoviesTv(page: Int) {
        viewModelScope.launch {
            when (val movies = repository.getNewMovies(page)) {
                is Result.Success -> {
                    val data = movies.data.data
                    _newMovieList.value = data
                }
                is Result.Error -> {
                    newToastMessage(movies.exception)
                    Log.d("errornewmovies", movies.exception)
                }
                is Result.Internet -> {
                    newToastMessage("შეამოწმეთ ინტერნეტთან კავშირი")
                    getNewMoviesTv(page)
                }
            }
        }
    }

    fun getTopMoviesTv(page: Int) {
        viewModelScope.launch {
            when (val topMovies = repository.getTopMovies(page)) {
                is Result.Success -> {
                    val data = topMovies.data.data
                    _topMovieList.value = data
                }
                is Result.Error -> {
                    newToastMessage(topMovies.exception)
                    Log.d("errornewmovies", topMovies.exception)
                }
                is Result.Internet -> {
                    newToastMessage("შეამოწმეთ ინტერნეტთან კავშირი")
                    getTopMoviesTv(page)
                }
            }
        }
    }

    fun getTopTvShowsTv(page: Int) {
        viewModelScope.launch {
            when (val tvShows = repository.getTopTvShows(page)) {
                is Result.Success -> {
                    val data = tvShows.data.data
                    _tvShowList.value = data
                }
                is Result.Error -> {
                    newToastMessage(tvShows.exception)
                    Log.d("errornewtvshows", tvShows.exception)
                }
                is Result.Internet -> {
                    newToastMessage("შეამოწმეთ ინტერნეტთან კავშირი")
                    getTopTvShowsTv(page)
                }
            }
        }
    }
}