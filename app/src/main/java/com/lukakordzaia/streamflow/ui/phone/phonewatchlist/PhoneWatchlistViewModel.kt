package com.lukakordzaia.streamflow.ui.phone.phonewatchlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.utils.toWatchListModel
import kotlinx.coroutines.launch

class PhoneWatchlistViewModel : BaseViewModel() {
    val favoriteMoviesLoader = MutableLiveData<LoadingState>()
    val favoriteTvShowsLoader = MutableLiveData<LoadingState>()

    val noFavorites = MutableLiveData<Boolean>()

    private val _userWatchlist = MutableLiveData<List<SingleTitleModel>>()
    val userWatchlist: LiveData<List<SingleTitleModel>> = _userWatchlist

    fun onSingleTitlePressed(titleId: Int) {
        navigateToNewFragment(PhoneWatchlistFragmentDirections.actionFavoritesFragmentToSingleTitleFragmentNav(titleId))
    }

    fun onProfileButtonPressed() {
        navigateToNewFragment(PhoneWatchlistFragmentDirections.actionPhoneFavoritesFragmentToProfileFragmentNav())
    }

    fun getUserWatchlist(page: Int) {
        favoriteMoviesLoader.value = LoadingState.LOADING
        favoriteTvShowsLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            when (val watchlist = environment.watchlistRepository.getUserWatchlist(page)) {
                is Result.Success -> {
                    val data = watchlist.data.data

                    if (data.isNullOrEmpty()) {
                        noFavorites.value = true
                    } else {
                        _userWatchlist.value = data.toWatchListModel()
                    }

                    favoriteMoviesLoader.value = LoadingState.LOADED
                    favoriteTvShowsLoader.value = LoadingState.LOADED
                }
            }
        }
    }

    fun deleteWatchlistTitle(id: Int) {
        viewModelScope.launch {
            when (val delete = environment.watchlistRepository.deleteWatchlistTitle(id)) {
                is Result.Success -> {
                    getUserWatchlist(1)
                }
            }
        }
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