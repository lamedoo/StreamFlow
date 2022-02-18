package com.lukakordzaia.streamflowphone.ui.catalogue.cataloguedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.usecases.SingleGenreUseCase
import com.lukakordzaia.core.domain.usecases.SingleStudioUseCase
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.ResultDomain
import kotlinx.coroutines.launch

class SingleCatalogueViewModel(
    private val singleGenreUseCase: SingleGenreUseCase,
    private val singleStudioUseCase: SingleStudioUseCase
) : BaseViewModel() {
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
            when (val result = singleGenreUseCase.invoke(Pair(genreId, page))) {
                is ResultDomain.Success -> {
                    val data = result.data
                    fetchSingleCatalogueList.addAll(data)
                    _singleCatalogueList.value = fetchSingleCatalogueList
                    _hasMorePage.value = data[0].hasMorePage
                    setGeneralLoader(LoadingState.LOADED)
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                        else -> newToastMessage("ჟანრი - ${result.exception}")
                    }
                }
            }
        }
    }

    private fun getSingleStudio(studioId: Int, page: Int) {
        viewModelScope.launch {
            when (val result = singleStudioUseCase.invoke(Pair(studioId, page))) {
                is ResultDomain.Success -> {
                    val data = result.data
                    fetchSingleCatalogueList.addAll(data)
                    _singleCatalogueList.value = fetchSingleCatalogueList
                    _hasMorePage.value = data[0].hasMorePage
                    setGeneralLoader(LoadingState.LOADED)
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                        else -> newToastMessage("ჟანრი - ${result.exception}")
                    }
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