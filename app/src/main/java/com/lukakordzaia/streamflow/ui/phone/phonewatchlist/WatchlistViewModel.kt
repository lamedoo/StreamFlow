package com.lukakordzaia.streamflow.ui.phone.phonewatchlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.datamodels.SingleTitleModel
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.Result
import com.lukakordzaia.core.network.toWatchListModel
import kotlinx.coroutines.launch

class WatchlistViewModel : BaseViewModel() {
    val noFavorites = MutableLiveData<Boolean>()

    private val fetchUserWatchlist: MutableList<SingleTitleModel> = ArrayList()
    private val _userWatchlist = MutableLiveData<List<SingleTitleModel>>()
    val userWatchlist: LiveData<List<SingleTitleModel>> = _userWatchlist

    private val _hasMorePage = MutableLiveData(true)
    val hasMorePage: LiveData<Boolean> = _hasMorePage

    fun onSingleTitlePressed(titleId: Int) {
        navigateToNewFragment(PhoneWatchlistFragmentDirections.actionFavoritesFragmentToSingleTitleFragmentNav(titleId))
    }

    fun onProfileButtonPressed() {
        navigateToNewFragment(PhoneWatchlistFragmentDirections.actionPhoneFavoritesFragmentToProfileFragmentNav())
    }

    fun getUserWatchlist(page: Int, type: String, isTvDevice: Boolean) {
        setNoInternet(false)
        setGeneralLoader(LoadingState.LOADING)
        viewModelScope.launch {
            when (val watchlist = environment.watchlistRepository.getUserWatchlist(page, type)) {
                is Result.Success -> {
                    val data = watchlist.data.data

                    _hasMorePage.value = watchlist.data.meta.pagination.totalPages > watchlist.data.meta.pagination.currentPage

                    noFavorites.value = data.isNullOrEmpty()

                    if (!data.isNullOrEmpty()) {
                        if (!isTvDevice) {
                            fetchUserWatchlist.addAll(data.toWatchListModel())
                            _userWatchlist.value = fetchUserWatchlist
                        } else {
                            _userWatchlist.value = data.toWatchListModel()
                        }
                    }

                    setGeneralLoader(LoadingState.LOADED)
                }
                is Result.Internet -> {
                    setNoInternet(true)
                }
            }
        }
    }

    fun deleteWatchlistTitle(id: Int, position: Int) {
        viewModelScope.launch {
            when (environment.watchlistRepository.deleteWatchlistTitle(id)) {
                is Result.Success -> {
                    fetchUserWatchlist.removeAt(position)
                    _userWatchlist.value = fetchUserWatchlist
                }
            }
        }
    }

    fun clearWatchlist() {
        fetchUserWatchlist.clear()
        _userWatchlist.value = fetchUserWatchlist
    }
}