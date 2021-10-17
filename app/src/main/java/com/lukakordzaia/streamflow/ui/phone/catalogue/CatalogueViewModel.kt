package com.lukakordzaia.streamflow.ui.phone.catalogue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.models.imovies.response.categories.GetGenresResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.categories.GetTopStudiosResponse
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.utils.toTitleListModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CatalogueViewModel : BaseViewModel() {
    val trailersLoader = MutableLiveData<LoadingState>()
    val genresLoader = MutableLiveData<LoadingState>()
    val studiosLoader = MutableLiveData<LoadingState>()

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
        navigateToNewFragment(CatalogueFragmentDirections.actionCategoriesFragmentToSingleGenreFragment(genreId, genreName))
    }

    fun onSingleStudioPressed(studioId: Int, studioName: String) {
        navigateToNewFragment(CatalogueFragmentDirections.actionCategoriesFragmentToSingleStudioFragment(studioId, studioName))
    }

    fun onSingleTrailerInfoPressed(titleId: Int) {
        navigateToNewFragment(CatalogueFragmentDirections.actionCategoriesFragmentToSingleTitleFragmentNav(titleId))
    }

    private suspend fun getAllGenres() {
        when (val genres = environment.catalogueRepository.getAllGenres()) {
            is Result.Success -> {
                _allGenresList.postValue(genres.data.data)
            }
            is Result.Error -> {
                newToastMessage("ჟანრები - ${genres.exception}")
            }
            is Result.Internet -> {
                setNoInternet()
            }
        }
    }

    private suspend fun getTopStudios() {
        when (val studios = environment.catalogueRepository.getTopStudios()) {
            is Result.Success -> {
                _topStudioList.postValue(studios.data.data)
            }
            is Result.Error -> {
                newToastMessage("სტუდიები - ${studios.exception}")
            }
            is Result.Internet -> {
                setNoInternet()
            }
        }
    }

    private suspend fun getTopTrailers() {
        when (val trailers = environment.catalogueRepository.getTopTrailers()) {
            is Result.Success -> {
                val data = trailers.data.data
                _topTrailerList.postValue(data.toTitleListModel())
            }
            is Result.Error -> {
                newToastMessage("ტრეილერები - ${trailers.exception}")
            }
            is Result.Internet -> {
                setNoInternet()
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