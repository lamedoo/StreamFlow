package com.lukakordzaia.streamflowphone.ui.catalogue.cataloguedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.datamodels.SingleTitleModel
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.toTitleListModel
import kotlinx.coroutines.launch

class SingleCatalogueViewModel : BaseViewModel() {
    private val fetchSingleCatalogueList: MutableList<SingleTitleModel> = ArrayList()
    private val _singleCatalogueList = MutableLiveData<List<SingleTitleModel>>()
    val singleCatalogueList: LiveData<List<SingleTitleModel>> = _singleCatalogueList

    private val _hasMorePage = MutableLiveData(true)
    val hasMorePage: LiveData<Boolean> = _hasMorePage

    fun onSingleTitlePressed(titleId: Int) {
        navigateToNewFragment(SingleCatalogueFragmentDirections.actionSingleCatalogueFragmentToSingleTitleFragmentNav(titleId))
    }

    private fun getSingleGenre(genreId: Int, page: Int) {
        viewModelScope.launch {
            when (val singleGenre = environment.catalogueRepository.getSingleGenre(genreId, page)) {
                is ResultData.Success -> {
                    val data = singleGenre.data.data
                    fetchSingleCatalogueList.addAll(data.toTitleListModel())
                    _singleCatalogueList.value = fetchSingleCatalogueList
                    _hasMorePage.value = singleGenre.data.meta.pagination.totalPages!! > singleGenre.data.meta.pagination.currentPage!!
                    setGeneralLoader(LoadingState.LOADED)
                }
                is ResultData.Error -> {
                    newToastMessage("ჟანრი - ${singleGenre.exception}")
                }
                is ResultData.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    private fun getSingleStudio(studioId: Int, page: Int) {
        viewModelScope.launch {
            when (val singleStudio = environment.catalogueRepository.getSingleStudio(studioId, page)) {
                is ResultData.Success -> {
                    val data = singleStudio.data.data
                    fetchSingleCatalogueList.addAll(data.toTitleListModel())
                    _singleCatalogueList.value = fetchSingleCatalogueList
                    _hasMorePage.value = singleStudio.data.meta.pagination.totalPages!! > singleStudio.data.meta.pagination.currentPage!!
                    setGeneralLoader(LoadingState.LOADED)
                }
                is ResultData.Error -> {
                    newToastMessage("სტუდია - ${singleStudio.exception}")
                }
                is ResultData.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    fun fetchContent(catalogueType: Int, catalogueId: Int, page: Int) {
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
}