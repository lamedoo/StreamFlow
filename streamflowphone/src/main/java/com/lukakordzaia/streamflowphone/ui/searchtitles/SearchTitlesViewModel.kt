package com.lukakordzaia.streamflowphone.ui.searchtitles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.usecases.SearchTitleUseCase
import com.lukakordzaia.core.domain.usecases.TopFranchisesUseCase
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.response.categories.GetTopFranchisesResponse
import com.lukakordzaia.core.utils.AppConstants
import kotlinx.coroutines.launch

class SearchTitlesViewModel(
    private val searchTitleUseCase: SearchTitleUseCase,
    private val topFranchisesUseCase: TopFranchisesUseCase
) : BaseViewModel() {
    val franchisesLoader = MutableLiveData<LoadingState>()

    private val _searchList = MutableLiveData<List<SingleTitleModel>>()
    val searchList: LiveData<List<SingleTitleModel>> = _searchList

    private val fetchSearchGetTitlesResponse: MutableList<SingleTitleModel> = ArrayList()

    private val _franchiseList = MutableLiveData<List<GetTopFranchisesResponse.Data>>()
    val getTopFranchisesResponse: LiveData<List<GetTopFranchisesResponse.Data>> = _franchiseList

    fun onSingleTitlePressed(titleId: Int) {
        navigateToNewFragment(SearchTitlesFragmentDirections.actionSearchTitlesFragmentToSingleTitleFragmentNav(titleId))
    }

    fun clearSearchResults() {
        fetchSearchGetTitlesResponse.clear()
        _searchList.value = fetchSearchGetTitlesResponse
    }

    fun getSearchTitles(keywords: String, page: Int) {
        viewModelScope.launch {
            setGeneralLoader(LoadingState.LOADING)
            when (val result = searchTitleUseCase.invoke(Pair(keywords, page))) {
                is ResultDomain.Success -> {
                    fetchSearchGetTitlesResponse.addAll(result.data)
                    _searchList.value = fetchSearchGetTitlesResponse
                    setGeneralLoader(LoadingState.LOADED)
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                        else -> newToastMessage("ძებნა - ${result.exception}")
                    }
                }
            }
        }
    }

    fun getTopFranchises() {
        viewModelScope.launch {
            franchisesLoader.value = LoadingState.LOADING
            when (val result = topFranchisesUseCase.invoke()) {
                is ResultDomain.Success -> {
                    val data = result.data.data
                    _franchiseList.value = data
                    franchisesLoader.value = LoadingState.LOADED
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                        else -> newToastMessage("ფრანჩიზები - ${result.exception}")
                    }
                }
            }
        }
    }
}