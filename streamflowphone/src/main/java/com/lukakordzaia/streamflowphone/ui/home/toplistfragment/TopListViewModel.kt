package com.lukakordzaia.streamflowphone.ui.home.toplistfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.utils.AppConstants
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.usecases.NewMoviesUseCase
import com.lukakordzaia.core.domain.usecases.TopTvShowsUseCase
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.ResultDomain
import kotlinx.coroutines.launch

class TopListViewModel(
    private val newMoviesUseCase: NewMoviesUseCase,
    private val topMoviesUseCase: NewMoviesUseCase,
    private val topTvShowsUseCase: TopTvShowsUseCase
) : BaseViewModel() {
    private val fetchList: MutableList<SingleTitleModel> = ArrayList()
    private val _list = MutableLiveData<List<SingleTitleModel>>()
    val list: LiveData<List<SingleTitleModel>> = _list

    fun onSingleTitlePressed(titleId: Int) {
        navigateToNewFragment(TopListFragmentDirections.actionTopListFragmentToSingleTitleFragmentNav(titleId))
    }

    private fun getNewMovies(page: Int) {
        viewModelScope.launch {
            setGeneralLoader(LoadingState.LOADING)
            when (val result = newMoviesUseCase.invoke(page)) {
                is ResultDomain.Success -> {
                    fetchList.addAll(result.data)
                    _list.value = fetchList
                    setGeneralLoader(LoadingState.LOADED)
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                        else -> newToastMessage(result.exception)
                    }
                }
            }
        }
    }

    private fun getTopMovies(page: Int) {
        viewModelScope.launch {
            setGeneralLoader(LoadingState.LOADING)
            when (val result = topMoviesUseCase.invoke(page)) {
                is ResultDomain.Success -> {
                    fetchList.addAll(result.data)
                    _list.value = fetchList
                    setGeneralLoader(LoadingState.LOADED)
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                        else -> newToastMessage(result.exception)
                    }
                }
            }
        }
    }

    private fun getTopTvShows(page: Int) {
        viewModelScope.launch {
            setGeneralLoader(LoadingState.LOADING)
            when (val result = topTvShowsUseCase.invoke(page)) {
                is ResultDomain.Success -> {
                    fetchList.addAll(result.data)
                    _list.value = fetchList
                    setGeneralLoader(LoadingState.LOADED)
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                        else -> newToastMessage(result.exception)
                    }
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