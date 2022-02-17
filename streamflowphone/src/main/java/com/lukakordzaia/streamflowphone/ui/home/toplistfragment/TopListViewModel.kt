package com.lukakordzaia.streamflowphone.ui.home.toplistfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.toTitleListModel
import kotlinx.coroutines.launch

class TopListViewModel : BaseViewModel() {
    private val fetchList: MutableList<SingleTitleModel> = ArrayList()
    private val _list = MutableLiveData<List<SingleTitleModel>>()
    val list: LiveData<List<SingleTitleModel>> = _list

    fun onSingleTitlePressed(titleId: Int) {
        navigateToNewFragment(TopListFragmentDirections.actionTopListFragmentToSingleTitleFragmentNav(titleId))
    }

    private fun getNewMovies(page: Int) {
        viewModelScope.launch {
            setGeneralLoader(LoadingState.LOADING)
            when (val newMovies = environment.homeRepository.getNewMovies(page)) {
                is ResultData.Success -> {
                    val data = newMovies.data.data
                    fetchList.addAll(data.toTitleListModel())
                    _list.value = fetchList
                    setGeneralLoader(LoadingState.LOADED)
                }
                is ResultData.Error -> {
                    newToastMessage("ახალი ფილმები - ${newMovies.exception}")
                }
                is ResultData.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    private fun getTopMovies(page: Int) {
        viewModelScope.launch {
            setGeneralLoader(LoadingState.LOADING)
            when (val topMovies = environment.homeRepository.getTopMovies(page)) {
                is ResultData.Success -> {
                    val data = topMovies.data.data
                    fetchList.addAll(data.toTitleListModel())
                    _list.value = fetchList
                    setGeneralLoader(LoadingState.LOADED)
                }
                is ResultData.Error -> {
                    newToastMessage("ტოპ ფილმები - ${topMovies.exception}")
                }
                is ResultData.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    private fun getTopTvShows(page: Int) {
        viewModelScope.launch {
            setGeneralLoader(LoadingState.LOADING)
            when (val topTvShows = environment.homeRepository.getTopTvShows(page)) {
                is ResultData.Success -> {
                    val data = topTvShows.data.data
                    fetchList.addAll(data.toTitleListModel())
                    _list.value = fetchList
                    setGeneralLoader(LoadingState.LOADED)
                }
                is ResultData.Error -> {
                    newToastMessage("ტოპ სერიალები- ${topTvShows.exception}")
                }
                is ResultData.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    fun fetchContent(type: Int, page: Int) {
        when (type) {
            AppConstants.LIST_NEW_MOVIES -> {
                getNewMovies(page)
            }
            AppConstants.LIST_TOP_MOVIES -> {
                getTopMovies(page)
            }
            AppConstants.LIST_TOP_TV_SHOWS -> {
                getTopTvShows(page)
            }
        }
    }
}