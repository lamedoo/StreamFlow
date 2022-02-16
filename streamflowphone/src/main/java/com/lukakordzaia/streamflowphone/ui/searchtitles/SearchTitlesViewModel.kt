package com.lukakordzaia.streamflowphone.ui.searchtitles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.datamodels.SingleTitleModel
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.models.imovies.response.categories.GetTopFranchisesResponse
import com.lukakordzaia.core.network.toTitleListModel
import kotlinx.coroutines.launch

class SearchTitlesViewModel : BaseViewModel() {
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
            when (val search = environment.searchRepository.getSearchTitles(keywords, page)) {
                is ResultData.Success -> {
                    val data = search.data.data
                    fetchSearchGetTitlesResponse.addAll(data.toTitleListModel())
                    _searchList.value = fetchSearchGetTitlesResponse
                    setGeneralLoader(LoadingState.LOADED)
                }
                is ResultData.Error -> {
                    newToastMessage("ძიება - ${search.exception}")
                }
                is ResultData.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    fun getTopFranchises() {
        viewModelScope.launch {
            franchisesLoader.value = LoadingState.LOADING
            when (val franchises = environment.searchRepository.getTopFranchises()) {
                is ResultData.Success -> {
                    val data = franchises.data.data
                    _franchiseList.value = data
                    franchisesLoader.value = LoadingState.LOADED
                }
                is ResultData.Error -> {
                    newToastMessage("ძიება - ${franchises.exception}")
                }
                is ResultData.Internet -> {
                    setNoInternet()
                }
            }
        }
    }
}