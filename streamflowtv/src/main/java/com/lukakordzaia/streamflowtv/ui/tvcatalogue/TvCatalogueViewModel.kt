package com.lukakordzaia.streamflowtv.ui.tvcatalogue

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

class TvCatalogueViewModel(
    private val newMoviesUseCase: NewMoviesUseCase,
    private val topMoviesUseCase: NewMoviesUseCase,
    private val topTvShowsUseCase: TopTvShowsUseCase
) : BaseViewModel() {
    private val _tvCatalogueList = MutableLiveData<List<SingleTitleModel>>()
    val tvCatalogueList: LiveData<List<SingleTitleModel>> = _tvCatalogueList

    fun getNewMovies(page: Int) {
        viewModelScope.launch {
            setGeneralLoader(LoadingState.LOADING)
            when (val result = newMoviesUseCase.invoke(page)) {
                is ResultDomain.Success -> {
                    _tvCatalogueList.value = result.data
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

    fun getTopMovies(page: Int) {
        viewModelScope.launch {
            setGeneralLoader(LoadingState.LOADING)
            when (val result = topMoviesUseCase.invoke(page)) {
                is ResultDomain.Success -> {
                    _tvCatalogueList.value = result.data
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

    fun getTopTvShows(page: Int) {
        viewModelScope.launch {
            setGeneralLoader(LoadingState.LOADING)
            when (val result = topTvShowsUseCase.invoke(page)) {
                is ResultDomain.Success -> {
                    _tvCatalogueList.value = result.data
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