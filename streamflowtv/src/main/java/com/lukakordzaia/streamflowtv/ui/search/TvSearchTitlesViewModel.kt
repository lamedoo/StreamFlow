package com.lukakordzaia.streamflowtv.ui.search

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

class TvSearchTitlesViewModel : BaseViewModel() {
    val franchisesLoader = MutableLiveData<LoadingState>()

    private val _searchList = MutableLiveData<List<SingleTitleModel>>()
    val searchList: LiveData<List<SingleTitleModel>> = _searchList

    private val fetchSearchGetTitlesResponse: MutableList<SingleTitleModel> = ArrayList()

    private val _franchiseList = MutableLiveData<List<GetTopFranchisesResponse.Data>>()
    val getTopFranchisesResponse: LiveData<List<GetTopFranchisesResponse.Data>> = _franchiseList

    fun clearSearchResults() {
        fetchSearchGetTitlesResponse.clear()
        _searchList.value = fetchSearchGetTitlesResponse
    }

    fun getSearchTitlesTv(keywords: String, page: Int) {
        setNoInternet(false)
        viewModelScope.launch {
            when (val searchTv = environment.searchRepository.getSearchTitles(keywords, page)) {
                is Result.Success -> {
                    val data = searchTv.data.data
                    _searchList.value = data.toTitleListModel()
                }
                is Result.Error -> {
                    newToastMessage("ძიება - ${searchTv.exception}")
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
        }
    }
}