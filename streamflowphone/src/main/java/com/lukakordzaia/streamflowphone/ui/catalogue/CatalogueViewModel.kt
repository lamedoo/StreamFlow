package com.lukakordzaia.streamflowphone.ui.catalogue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.models.imovies.response.categories.GetGenresResponse
import com.lukakordzaia.core.network.models.imovies.response.categories.GetTopStudiosResponse
import com.lukakordzaia.core.network.toTitleListModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CatalogueViewModel : BaseViewModel() {
    private val _allGenresList = MutableLiveData<List<GetGenresResponse.Data>>()
    val allGenresList: LiveData<List<GetGenresResponse.Data>> = _allGenresList

    private val _topStudioList = MutableLiveData<List<GetTopStudiosResponse.Data>>()
    val topGetTopStudiosResponse: LiveData<List<GetTopStudiosResponse.Data>> = _topStudioList

    private val _topTrailerList = MutableLiveData<List<SingleTitleModel>>()
    val topTrailerList: LiveData<List<SingleTitleModel>> = _topTrailerList

    init {
        fetchContent()
    }

    fun onSingleGenrePressed(genreId: Int, genreName: String) {
        navigateToNewFragment(CatalogueFragmentDirections.actionCategoriesFragmentToSingleCatalogueFragment(AppConstants.LIST_SINGLE_GENRE, genreId, genreName))
    }

    fun onSingleStudioPressed(studioId: Int, studioName: String) {
        navigateToNewFragment(CatalogueFragmentDirections.actionCategoriesFragmentToSingleCatalogueFragment(AppConstants.LIST_SINGLE_STUDIO, studioId, studioName))
    }

    fun onSingleTrailerInfoPressed(titleId: Int) {
        navigateToNewFragment(CatalogueFragmentDirections.actionCategoriesFragmentToSingleTitleFragmentNav(titleId))
    }

    private suspend fun getTopTrailers() {
        when (val trailers = environment.catalogueRepository.getTopTrailers()) {
            is ResultData.Success -> {
                val data = trailers.data.data
                _topTrailerList.postValue(data.toTitleListModel())
            }
            is ResultData.Error -> {
                newToastMessage("ტრეილერები - ${trailers.exception}")
            }
            is ResultData.Internet -> {
                setNoInternet()
            }
        }
    }

    private suspend fun getAllGenres() {
        when (val genres = environment.catalogueRepository.getAllGenres()) {
            is ResultData.Success -> {
                _allGenresList.postValue(genres.data.data)
            }
            is ResultData.Error -> {
                newToastMessage("ჟანრები - ${genres.exception}")
            }
        }
    }

    private suspend fun getTopStudios() {
        when (val studios = environment.catalogueRepository.getTopStudios()) {
            is ResultData.Success -> {
                _topStudioList.postValue(studios.data.data)
            }
            is ResultData.Error -> {
                newToastMessage("სტუდიები - ${studios.exception}")
            }
        }
    }

    fun fetchContent() {
        setGeneralLoader(LoadingState.LOADING)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val getData = viewModelScope.launch {
                    getTopTrailers()
                    getAllGenres()
                    getTopStudios()
                }
                getData.join()
                setGeneralLoader(LoadingState.LOADED)
            }
        }
    }
}