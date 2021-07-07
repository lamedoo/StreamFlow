package com.lukakordzaia.streamflow.ui.phone.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.datamodels.GenreList
import com.lukakordzaia.streamflow.datamodels.StudioList
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.datamodels.VideoPlayerData
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.repository.CategoriesRepository
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class CategoriesViewModel(private val repository: CategoriesRepository) : BaseViewModel() {

    val trailersLoader = MutableLiveData<LoadingState>()
    val genresLoader = MutableLiveData<LoadingState>()
    val studiosLoader = MutableLiveData<LoadingState>()

    private val _allGenresList = MutableLiveData<List<GenreList.Data>>()
    val allGenresList: LiveData<List<GenreList.Data>> = _allGenresList

    private val _topStudioList = MutableLiveData<List<StudioList.Data>>()
    val topStudioList: LiveData<List<StudioList.Data>> = _topStudioList

    private val _topTrailerList = MutableLiveData<List<TitleList.Data>>()
    val topTrailerList: LiveData<List<TitleList.Data>> = _topTrailerList

    fun onSingleGenrePressed(genreId: Int, genreName: String) {
        navigateToNewFragment(CategoriesFragmentDirections.actionCategoriesFragmentToSingleGenreFragment(genreId, genreName))
    }

    fun onSingleStudioPressed(studioId: Int, studioName: String) {
        navigateToNewFragment(CategoriesFragmentDirections.actionCategoriesFragmentToSingleStudioFragment(studioId, studioName))
    }

    fun onSingleTrailerInfoPressed(titleId: Int) {
        navigateToNewFragment(CategoriesFragmentDirections.actionCategoriesFragmentToSingleTitleFragmentNav(titleId))
    }

    fun getAllGenres() {
        viewModelScope.launch {
            genresLoader.value = LoadingState.LOADING
            when (val genres = repository.getAllGenres()) {
                is Result.Success -> {
                    _allGenresList.value = genres.data.data
                    genresLoader.value = LoadingState.LOADED
                }
                is Result.Error -> {
                    newToastMessage("ჟანრები - ${genres.exception}")
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    fun getTopStudios() {
        viewModelScope.launch {
            studiosLoader.value = LoadingState.LOADING
            when (val studios = repository.getTopStudios()) {
                is Result.Success -> {
                    _topStudioList.value = studios.data.data
                    studiosLoader.value = LoadingState.LOADED
                }
                is Result.Error -> {
                    newToastMessage("სტუდიები - ${studios.exception}")
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    fun getTopTrailers() {
        trailersLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            when (val trailers = repository.getTopTrailers()) {
                is Result.Success -> {
                    _topTrailerList.value = trailers.data.data
                    trailersLoader.value = LoadingState.LOADED
                }
                is Result.Error -> {
                    newToastMessage("ტრეილერები - ${trailers.exception}")
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    fun refreshContent() {
        getTopTrailers()
        getAllGenres()
        getTopStudios()
    }
}