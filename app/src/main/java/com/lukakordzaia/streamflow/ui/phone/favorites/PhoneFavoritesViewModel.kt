package com.lukakordzaia.streamflow.ui.phone.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.interfaces.FavoritesCallBack
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.utils.toSingleTitleModel
import kotlinx.coroutines.launch

class PhoneFavoritesViewModel : BaseViewModel() {
    val favoriteMoviesLoader = MutableLiveData<LoadingState>()
    val favoriteTvShowsLoader = MutableLiveData<LoadingState>()

    val favoriteNoMovies = MutableLiveData<Boolean>()
    val favoriteNoTvShows = MutableLiveData<Boolean>()

    private val _movieResult = MutableLiveData<List<SingleTitleModel>>()
    val movieResult: LiveData<List<SingleTitleModel>> = _movieResult

    private val _tvShowResult = MutableLiveData<List<SingleTitleModel>>()
    val tvShowResult: LiveData<List<SingleTitleModel>> = _tvShowResult

    fun onSingleTitlePressed(titleId: Int) {
        navigateToNewFragment(PhoneFavoritesFragmentDirections.actionFavoritesFragmentToSingleTitleFragmentNav(titleId))
    }

    fun onProfileButtonPressed() {
        navigateToNewFragment(PhoneFavoritesFragmentDirections.actionFavoritesFragmentToProfileFragment())
    }

    fun getFavTitlesFromFirestore() {
        environment.favoritesRepository.getTitlesFromFavorites(currentUser()!!.uid, object :
            FavoritesCallBack {
            override fun moviesList(movies: MutableList<Int>) {
                favoriteMoviesLoader.value = LoadingState.LOADING
                if (movies.isNullOrEmpty()) {
                    favoriteMoviesLoader.value = LoadingState.LOADED
                    favoriteNoMovies.value = true
                } else {
                    favoriteNoMovies.value = false
                    val fetchMovieResult: MutableList<SingleTitleModel> = ArrayList()
                    viewModelScope.launch {
                        movies.forEach {
                            when (val moviesData = environment.singleTitleRepository.getSingleTitleData(it)) {
                                is Result.Success -> {
                                    val data = moviesData.data
                                    fetchMovieResult.add(data.toSingleTitleModel())

                                    _movieResult.value = fetchMovieResult
                                }
                            }
                        }
                        favoriteMoviesLoader.value = LoadingState.LOADED
                    }
                }
            }

            override fun tvShowsList(tvShows: MutableList<Int>) {
                favoriteTvShowsLoader.value = LoadingState.LOADING
                if (tvShows.isNullOrEmpty()) {
                    favoriteTvShowsLoader.value = LoadingState.LOADED
                    favoriteNoTvShows.value = true
                } else {
                    favoriteNoTvShows.value = false
                    val fetchTvShowResult: MutableList<SingleTitleModel> = ArrayList()
                    viewModelScope.launch {
                        tvShows.forEach {
                            when (val tvShowsData = environment.singleTitleRepository.getSingleTitleData(it)) {
                                is Result.Success -> {
                                    val data = tvShowsData.data
                                    fetchTvShowResult.add(data.toSingleTitleModel())

                                    _tvShowResult.value = fetchTvShowResult
                                }
                            }
                            favoriteTvShowsLoader.value = LoadingState.LOADED
                        }
                    }
                }
            }

        })
    }

    fun removeFavTitleFromFirestore(titleId: Int) {
        viewModelScope.launch {
            val removeFromFavorites = environment.favoritesRepository.removeTitleFromFavorites(currentUser()!!.uid, titleId)
            if (removeFromFavorites) {
                newToastMessage("წარმატებით წაიშალა ფავორიტებიდან")
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