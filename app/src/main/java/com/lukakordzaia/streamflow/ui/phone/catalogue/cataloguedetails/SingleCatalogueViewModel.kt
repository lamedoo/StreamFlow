package com.lukakordzaia.streamflow.ui.phone.catalogue.cataloguedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.utils.AppConstants
import com.lukakordzaia.streamflow.utils.toTitleListModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SingleCatalogueViewModel : BaseViewModel() {
    private val fetchSingleCatalogueList: MutableList<SingleTitleModel> = ArrayList()
    private val _singleCatalogueList = MutableLiveData<List<SingleTitleModel>>()
    val singleCatalogueList: LiveData<List<SingleTitleModel>> = _singleCatalogueList

    private val _singleGenreAnimation = MutableLiveData<List<SingleTitleModel>>()
    val singleGenreAnimation: LiveData<List<SingleTitleModel>> = _singleGenreAnimation

    private val _singleGenreComedy = MutableLiveData<List<SingleTitleModel>>()
    val singleGenreComedy: LiveData<List<SingleTitleModel>> = _singleGenreComedy

    private val _singleGenreMelodrama = MutableLiveData<List<SingleTitleModel>>()
    val singleGenreMelodrama: LiveData<List<SingleTitleModel>> = _singleGenreMelodrama

    private val _singleGenreHorror = MutableLiveData<List<SingleTitleModel>>()
    val singleGenreHorror: LiveData<List<SingleTitleModel>> = _singleGenreHorror

    private val _singleGenreAdventure = MutableLiveData<List<SingleTitleModel>>()
    val singleGenreAdventure: LiveData<List<SingleTitleModel>> = _singleGenreAdventure

    private val _singleGenreAction = MutableLiveData<List<SingleTitleModel>>()
    val singleGenreAction: LiveData<List<SingleTitleModel>> = _singleGenreAction

    private val _hasMorePage = MutableLiveData(true)
    val hasMorePage: LiveData<Boolean> = _hasMorePage

    fun onSingleTitlePressed(titleId: Int) {
        navigateToNewFragment(SingleCatalogueFragmentDirections.actionSingleCatalogueFragmentToSingleTitleFragmentNav(titleId))
    }

    private fun getSingleGenre(genreId: Int, page: Int) {
        viewModelScope.launch {
            when (val singleGenre = environment.catalogueRepository.getSingleGenre(genreId, page)) {
                is Result.Success -> {
                    val data = singleGenre.data.data
                    fetchSingleCatalogueList.addAll(data.toTitleListModel())
                    _singleCatalogueList.value = fetchSingleCatalogueList
                    _hasMorePage.value = singleGenre.data.meta.pagination.totalPages!! > singleGenre.data.meta.pagination.currentPage!!
                    setGeneralLoader(LoadingState.LOADED)
                }
                is Result.Error -> {
                    newToastMessage("ჟანრი - ${singleGenre.exception}")
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    private suspend fun getSingleGenreForTv(genreId: Int, page: Int = 1) {
            when (val singleGenre = environment.catalogueRepository.getSingleGenre(genreId, page)) {
                is Result.Success -> {
                    val data = singleGenre.data.data
                    when (genreId) {
                        265 -> _singleGenreAnimation.value = data.toTitleListModel()
                        258 -> _singleGenreComedy.value = data.toTitleListModel()
                        260 -> _singleGenreMelodrama.value = data.toTitleListModel()
                        255 -> _singleGenreHorror.value = data.toTitleListModel()
                        266 -> _singleGenreAdventure.value = data.toTitleListModel()
                        248 -> _singleGenreAction.value = data.toTitleListModel()
                    }
                }
                is Result.Error -> {
                    newToastMessage("ჟანრი - ${singleGenre.exception}")
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
    }

    private fun getSingleStudio(studioId: Int, page: Int) {
        viewModelScope.launch {
            when (val singleStudio = environment.catalogueRepository.getSingleStudio(studioId, page)) {
                is Result.Success -> {
                    val data = singleStudio.data.data
                    fetchSingleCatalogueList.addAll(data.toTitleListModel())
                    _singleCatalogueList.value = fetchSingleCatalogueList
                    _hasMorePage.value = singleStudio.data.meta.pagination.totalPages!! > singleStudio.data.meta.pagination.currentPage!!
                    setGeneralLoader(LoadingState.LOADED)
                }
                is Result.Error -> {
                    newToastMessage("სტუდია - ${singleStudio.exception}")
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    fun fetchContent(catalogueType: Int, catalogueId: Int, page: Int) {
        setNoInternet(false)
        setGeneralLoader(LoadingState.LOADING)
        when (catalogueType) {
            AppConstants.LIST_SINGLE_STUDIO -> {
                getSingleStudio(catalogueId, page)
            }
            AppConstants.LIST_SINGLE_GENRE -> {
                getSingleGenre(catalogueId, page)
            }
        }
    }

    fun fetchContentTv() {
        setNoInternet(false)
        setGeneralLoader(LoadingState.LOADING)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val getData = viewModelScope.launch {
                    getSingleGenreForTv(265)
                    getSingleGenreForTv(258)
                    getSingleGenreForTv(260)
                    getSingleGenreForTv(255)
                    getSingleGenreForTv(266)
                    getSingleGenreForTv(248)
                }
                getData.join()
                setGeneralLoader(LoadingState.LOADED)
            }
        }
    }
}