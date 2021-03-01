package com.lukakordzaia.streamflow.ui.phone.searchtitles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.datamodels.FranchiseList
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.repository.SearchTitleRepository
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class SearchTitlesViewModel(private val repository: SearchTitleRepository) : BaseViewModel() {
    val searchLoader = MutableLiveData<LoadingState>()
    val franchisesLoader = MutableLiveData<LoadingState>()

    private val _searchList = MutableLiveData<List<TitleList.Data>>()
    val searchList: LiveData<List<TitleList.Data>> = _searchList

    private val fetchSearchTitleList: MutableList<TitleList.Data> = ArrayList()

    private val _franchiseList = MutableLiveData<List<FranchiseList.Data>>()
    val franchiseList: LiveData<List<FranchiseList.Data>> = _franchiseList

    fun onSingleTitlePressed(titleId: Int) {
        navigateToNewFragment(SearchTitlesFragmentDirections.actionSearchTitlesFragmentToSingleTitleFragmentNav(titleId))
    }

    fun clearSearchResults() {
        fetchSearchTitleList.clear()
        _searchList.value = mutableListOf()
    }

    fun getSearchTitles(keywords: String, page: Int) {
        viewModelScope.launch {
            searchLoader.value = LoadingState.LOADING
            when (val search = repository.getSearchTitles(keywords, page)) {
                is Result.Success -> {
                    val data = search.data.data
                    data.forEach {
                        fetchSearchTitleList.add(it)
                    }
                    _searchList.value = fetchSearchTitleList
                    searchLoader.value = LoadingState.LOADED
                }
                is Result.Error -> {
                    newToastMessage("ძიება - ${search.exception}")
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    fun getSearchTitlesTv(keywords: String, page: Int) {
        viewModelScope.launch {
            when (val searchTv = repository.getSearchTitles(keywords, page)) {
                is Result.Success -> {
                    val data = searchTv.data.data
                    _searchList.value = data
                }
                is Result.Error -> {
                    newToastMessage("ძიება - ${searchTv.exception}")
                }
            }
        }
    }

    fun getTopFranchises() {
        viewModelScope.launch {
            franchisesLoader.value = LoadingState.LOADING
            when (val franchises = repository.getTopFranchises()) {
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

    fun refreshContent() {
        getTopFranchises()
    }
}