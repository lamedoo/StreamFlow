package com.lukakordzaia.streamflowphone.ui.phonewatchlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.usecases.DeleteWatchlistUseCase
import com.lukakordzaia.core.domain.usecases.WatchlistUseCase
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.utils.AppConstants
import kotlinx.coroutines.launch

class WatchlistViewModel(
    private val watchlistUseCase: WatchlistUseCase,
    private val deleteWatchlistUseCase: DeleteWatchlistUseCase
) : BaseViewModel() {
    val noFavorites = MutableLiveData<Boolean>()

    private val fetchUserWatchlist: MutableList<SingleTitleModel> = ArrayList()
    private val _userWatchlist = MutableLiveData<List<SingleTitleModel>>()
    val userWatchlist: LiveData<List<SingleTitleModel>> = _userWatchlist

    private val _hasMorePage = MutableLiveData(true)
    val hasMorePage: LiveData<Boolean> = _hasMorePage

    private val _selectedTab = MutableLiveData<String>()
    val selectedTab: LiveData<String> = _selectedTab

    init {
        setSelectedTab(AppConstants.WATCHLIST_MOVIES)
    }

    fun onSingleTitlePressed(titleId: Int) {
        navigateToNewFragment(PhoneWatchlistFragmentDirections.actionFavoritesFragmentToSingleTitleFragmentNav(titleId))
    }

    fun onProfileButtonPressed() {
        navigateToNewFragment(PhoneWatchlistFragmentDirections.actionPhoneFavoritesFragmentToProfileFragmentNav())
    }

    fun getUserWatchlist(page: Int, type: String) {
        setGeneralLoader(LoadingState.LOADING)
        viewModelScope.launch {
            when (val result = watchlistUseCase.invoke(Pair(page, type))) {
                is ResultDomain.Success -> {
                    val data = result.data

                    _hasMorePage.value = data[0].hasMorePage

                    noFavorites.value = data.isNullOrEmpty()

                    fetchUserWatchlist.addAll(data)
                    _userWatchlist.value = fetchUserWatchlist

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

    fun deleteWatchlistTitle(id: Int, position: Int) {
        viewModelScope.launch {
            when (val result = deleteWatchlistUseCase.invoke(id)) {
                is ResultDomain.Success -> {
                    fetchUserWatchlist.removeAt(position)
                    _userWatchlist.value = fetchUserWatchlist
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                        else -> newToastMessage("ვერ მოხერხდა წაშლა - ${result.exception}")
                    }
                }
            }
        }
    }

    fun clearWatchlist() {
        fetchUserWatchlist.clear()
        _userWatchlist.value = fetchUserWatchlist
    }

    fun setSelectedTab(type: String) {
        _selectedTab.value = type
    }
}