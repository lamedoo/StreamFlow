package com.lukakordzaia.streamflowtv.ui.tvwatchlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.usecases.WatchlistUseCase
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.utils.AppConstants
import kotlinx.coroutines.launch

class TvWatchlistViewModel(
    private val watchlistUseCase: WatchlistUseCase
) : BaseViewModel() {
    val hasFavorites = MutableLiveData<Boolean>()

    private val _userWatchlist = MutableLiveData<List<SingleTitleModel>>()
    val userWatchlist: LiveData<List<SingleTitleModel>> = _userWatchlist

    fun getUserWatchlist(page: Int, type: String) {
        setGeneralLoader(LoadingState.LOADING)
        viewModelScope.launch {
            when (val result = watchlistUseCase.invoke(Pair(page, type))) {
                is ResultDomain.Success -> {
                    val data = result.data

                    _userWatchlist.value = data
                    if (page == 1) {
                        hasFavorites.value = data.isNotEmpty()
                    }

                    setGeneralLoader(LoadingState.LOADED)
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                        else -> newToastMessage("შენახული სია - ${result.exception}")
                    }
                }
            }
        }
    }
}