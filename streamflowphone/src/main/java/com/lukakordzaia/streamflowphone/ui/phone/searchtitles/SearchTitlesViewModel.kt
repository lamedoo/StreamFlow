package com.lukakordzaia.streamflowphone.ui.phone.searchtitles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.datamodels.SingleTitleModel
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.Result
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
        setNoInternet(false)
        viewModelScope.launch {
            setGeneralLoader(LoadingState.LOADING)
            when (val search = environment.searchRepository.getSearchTitles(keywords, page)) {
                is Result.Success -> {
                    val data = search.data.data
                    fetchSearchGetTitlesResponse.addAll(data.toTitleListModel())
                    _searchList.value = fetchSearchGetTitlesResponse
                    setGeneralLoader(LoadingState.LOADED)
                }
                is Result.Error -> {
                    newToastMessage("ძიება - ${search.exception}")
                }
                is Result.Internet -> {
                    setNoInternet(true)
                }
            }
        }
    }

    fun getTopFranchises() {
        setNoInternet(false)
        viewModelScope.launch {
            franchisesLoader.value = LoadingState.LOADING
            when (val franchises = environment.searchRepository.getTopFranchises()) {
                is Result.Success -> {
                    val data = franchises.data.data
                    _franchiseList.value = data
                    franchisesLoader.value = LoadingState.LOADED
                }
                is Result.Error -> {
                    newToastMessage("ძიება - ${franchises.exception}")
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
        }
    }
}