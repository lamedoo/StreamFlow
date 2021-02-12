package com.lukakordzaia.streamflow.ui.phone.searchtitles

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.datamodels.FranchiseList
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.repository.SearchTitleRepository
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class SearchTitlesViewModel(private val repository: SearchTitleRepository) : BaseViewModel() {
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
    }

    fun getSearchTitles(keywords: String, page: Int) {
        viewModelScope.launch {
            when (val movies = repository.getSearchTitles(keywords, page)) {
                is Result.Success -> {
                    val data = movies.data.data
                    data.forEach {
                        fetchSearchTitleList.add(it)
                    }
                    _searchList.value = fetchSearchTitleList
                }
                is Result.Error -> {
                    Log.d("errornewmovies", movies.exception)
                }
            }
        }
    }

    fun getSearchTitlesTv(keywords: String, page: Int) {
        viewModelScope.launch {
            when (val movies = repository.getSearchTitles(keywords, page)) {
                is Result.Success -> {
                    val data = movies.data.data
                    _searchList.value = data
                }
                is Result.Error -> {
                    Log.d("errorsearchtitles", movies.exception)
                }
            }
        }
    }

    fun getTopFranchises() {
        viewModelScope.launch {
            when (val franchises = repository.getTopFranchises()) {
                is Result.Success -> {
                    val data = franchises.data.data
                    _franchiseList.value = data
                }
                is Result.Error -> {
                    Log.d("errorfranchises", franchises.exception)
                }
            }
        }
    }
}