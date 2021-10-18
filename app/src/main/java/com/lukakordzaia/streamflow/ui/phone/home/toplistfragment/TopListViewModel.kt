package com.lukakordzaia.streamflow.ui.phone.home.toplistfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.utils.toTitleListModel
import kotlinx.coroutines.launch

class TopListViewModel : BaseViewModel() {
    private val fetchList: MutableList<SingleTitleModel> = ArrayList()
    private val _list = MutableLiveData<List<SingleTitleModel>>()
    val list: LiveData<List<SingleTitleModel>> = _list

    fun onSingleTitlePressed(titleId: Int) {
        navigateToNewFragment(TopListFragmentDirections.actionTopListFragmentToSingleTitleFragmentNav(titleId))
    }

    fun getNewMovies(page: Int) {
        viewModelScope.launch {
            setGeneralLoader(LoadingState.LOADING)
            when (val newMovies = environment.homeRepository.getNewMovies(page)) {
                is Result.Success -> {
                    val data = newMovies.data.data
                    fetchList.addAll(data.toTitleListModel())
                    _list.value = fetchList
                    setGeneralLoader(LoadingState.LOADED)
                }
                is Result.Error -> {
                    newToastMessage("ახალი ფილმები - ${newMovies.exception}")
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    fun getTopMovies(page: Int) {
        viewModelScope.launch {
            setGeneralLoader(LoadingState.LOADING)
            when (val topMovies = environment.homeRepository.getTopMovies(page)) {
                is Result.Success -> {
                    val data = topMovies.data.data
                    fetchList.addAll(data.toTitleListModel())
                    _list.value = fetchList
                    setGeneralLoader(LoadingState.LOADED)
                }
                is Result.Error -> {
                    newToastMessage("ტოპ ფილმები - ${topMovies.exception}")
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    fun getTopTvShows(page: Int) {
        viewModelScope.launch {
            setGeneralLoader(LoadingState.LOADING)
            when (val topTvShows = environment.homeRepository.getTopTvShows(page)) {
                is Result.Success -> {
                    val data = topTvShows.data.data
                    fetchList.addAll(data.toTitleListModel())
                    _list.value = fetchList
                    setGeneralLoader(LoadingState.LOADED)
                }
                is Result.Error -> {
                    newToastMessage("ტოპ სერიალები- ${topTvShows.exception}")
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
        }
    }
}