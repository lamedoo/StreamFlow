package com.lukakordzaia.streamflowphone.ui.catalogue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.usecases.AllGenresUseCase
import com.lukakordzaia.core.domain.usecases.TopStudiosUseCase
import com.lukakordzaia.core.domain.usecases.TopTrailersUseCase
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.response.categories.GetGenresResponse
import com.lukakordzaia.core.network.models.imovies.response.categories.GetTopStudiosResponse
import kotlinx.coroutines.*

class CatalogueViewModel(
    private val topTrailersUseCase: TopTrailersUseCase,
    private val allGenresUseCase: AllGenresUseCase,
    private val topStudiosUseCase: TopStudiosUseCase
) : BaseViewModel() {
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
        when (val result = topTrailersUseCase.invoke()) {
            is ResultDomain.Success -> {
                _topTrailerList.postValue(result.data)
            }
            is ResultDomain.Error -> {
                when (result.exception) {
                    AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                    else -> newToastMessage("ტრეილერები - ${result.exception}")
                }
            }
        }
    }

    private suspend fun getAllGenres() {
        when (val result = allGenresUseCase.invoke()) {
            is ResultDomain.Success -> {
                _allGenresList.postValue(result.data)
            }
            is ResultDomain.Error -> {
                when (result.exception) {
                    AppConstants.NO_INTERNET_ERROR -> {}
                    else -> newToastMessage("ჟანრები - ${result.exception}")
                }
            }
        }
    }

    private suspend fun getTopStudios() {
        when (val result = topStudiosUseCase.invoke()) {
            is ResultDomain.Success -> {
                _topStudioList.postValue(result.data)
            }
            is ResultDomain.Error -> {
                when (result.exception) {
                    AppConstants.NO_INTERNET_ERROR -> {}
                    else -> newToastMessage("სტუდიები - ${result.exception}")
                }
            }
        }
    }

    fun fetchContent() {
        setGeneralLoader(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            coroutineScope {
                val trailersDeferred = async { getTopTrailers() }
                val genresDeferred = async { getAllGenres() }
                val studiosDeferred = async { getTopStudios() }

                trailersDeferred.await()
                genresDeferred.await()
                studiosDeferred.await()
            }
            setGeneralLoader(LoadingState.LOADED)
        }
    }
}