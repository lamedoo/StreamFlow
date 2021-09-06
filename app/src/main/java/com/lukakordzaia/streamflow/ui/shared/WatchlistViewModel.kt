package com.lukakordzaia.streamflow.ui.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.ui.phone.phonewatchlist.PhoneWatchlistFragmentDirections
import com.lukakordzaia.streamflow.utils.Event
import com.lukakordzaia.streamflow.utils.toWatchListModel
import kotlinx.coroutines.launch

class WatchlistViewModel : BaseViewModel() {
    val watchListLoader = MutableLiveData<LoadingState>()

    val noFavorites = MutableLiveData<Boolean>()

    private val fetchUserWatchlist: MutableList<SingleTitleModel> = ArrayList()
    private val _userWatchlist = MutableLiveData<List<SingleTitleModel>>()
    val userWatchlist: LiveData<List<SingleTitleModel>> = _userWatchlist

    private val _removedTitle = MutableLiveData<Event<Int>>()
    val removedTitle: LiveData<Event<Int>> = _removedTitle

    private val _hasMorePage = MutableLiveData(true)
    val hasMorePage: LiveData<Boolean> = _hasMorePage

    fun onSingleTitlePressed(titleId: Int) {
        navigateToNewFragment(PhoneWatchlistFragmentDirections.actionFavoritesFragmentToSingleTitleFragmentNav(titleId))
    }

    fun onProfileButtonPressed() {
        navigateToNewFragment(PhoneWatchlistFragmentDirections.actionPhoneFavoritesFragmentToProfileFragmentNav())
    }

    fun getUserWatchlist(page: Int, type: String) {
        watchListLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            when (val watchlist = environment.watchlistRepository.getUserWatchlist(page, type)) {
                is Result.Success -> {
                    val data = watchlist.data.data

                    _hasMorePage.value = watchlist.data.meta.pagination.totalPages > watchlist.data.meta.pagination.currentPage

                    noFavorites.value = data.isNullOrEmpty()

                    if (!data.isNullOrEmpty()) {
                        fetchUserWatchlist.addAll(data.toWatchListModel())
                        _userWatchlist.value = fetchUserWatchlist
                    }

                    watchListLoader.value = LoadingState.LOADED
                }
            }
        }
    }

    fun deleteWatchlistTitle(id: Int, position: Int) {
        viewModelScope.launch {
            when (val delete = environment.watchlistRepository.deleteWatchlistTitle(id)) {
                is Result.Success -> {
                    fetchUserWatchlist.removeAt(position)
                    _userWatchlist.value = fetchUserWatchlist

//                    _removedTitle.value = Event(position)
                }
            }
        }
    }

    fun clearWatchlist() {
        fetchUserWatchlist.clear()
        _userWatchlist.value = fetchUserWatchlist
    }


//    private fun removeMovieFromTraktList(movieFromTraktList: AddMovieToTraktList, accessToken: String) {
//        viewModelScope.launch {
//            when (val removeMovie = traktRepository.removeMovieFromTraktList(movieFromTraktList, accessToken)) {
//                is Result.Success -> {
//                    newToastMessage("ფილმი წაიშალა ფავორიტებიდან")
//                }
//            }
//        }
//    }
//
//    private fun removeTvShowFromTraktList(tvShowFromTraktList: AddTvShowToTraktList, accessToken: String) {
//        viewModelScope.launch {
//            when (val removeTvShow = traktRepository.removeTvShowFromTraktList(tvShowFromTraktList, accessToken)) {
//                is Result.Success -> {
//                    newToastMessage("სერიალი წაიშალა ფავორიტებიდან")
//                }
//            }
//        }
//    }
}