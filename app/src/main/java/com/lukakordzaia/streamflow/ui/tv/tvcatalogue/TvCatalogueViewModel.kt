package com.lukakordzaia.streamflow.ui.tv.tvcatalogue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.utils.toTitleListModel
import kotlinx.coroutines.launch

class TvCatalogueViewModel : BaseViewModel() {
    private val _newMovieList = MutableLiveData<List<SingleTitleModel>>()
    val newMovieList: LiveData<List<SingleTitleModel>> = _newMovieList

    private val _topMovieList = MutableLiveData<List<SingleTitleModel>>()
    val topMovieList: LiveData<List<SingleTitleModel>> = _topMovieList

    private val _tvShowList = MutableLiveData<List<SingleTitleModel>>()
    val tvShowList: LiveData<List<SingleTitleModel>> = _tvShowList

    fun getNewMoviesTv(page: Int) {
        viewModelScope.launch {
            when (val movies = environment.homeRepository.getNewMovies(page)) {
                is Result.Success -> {
                    val data = movies.data.data
                    _newMovieList.value = data.toTitleListModel()
                }
                is Result.Error -> {
                    newToastMessage(movies.exception)
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
            when (val topMovies = environment.homeRepository.getTopMovies(page)) {
                is Result.Success -> {
                    val data = topMovies.data.data
                    _topMovieList.value = data.toTitleListModel()
                }
                is Result.Error -> {
                    newToastMessage(topMovies.exception)
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
            when (val tvShows = environment.homeRepository.getTopTvShows(page)) {
                is Result.Success -> {
                    val data = tvShows.data.data
                    _tvShowList.value = data.toTitleListModel()
                }
                is Result.Error -> {
                    newToastMessage(tvShows.exception)
                }
                is Result.Internet -> {
                    newToastMessage("შეამოწმეთ ინტერნეტთან კავშირი")
                    getTopTvShowsTv(page)
                }
            }
        }
    }
}