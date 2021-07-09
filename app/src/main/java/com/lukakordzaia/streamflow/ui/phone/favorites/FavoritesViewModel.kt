package com.lukakordzaia.streamflow.ui.phone.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleResponse
import com.lukakordzaia.streamflow.interfaces.FavoritesCallBack
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.repository.FavoritesRepository
import com.lukakordzaia.streamflow.repository.TraktRepository
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class FavoritesViewModel(private val favoritesRepository: FavoritesRepository, private val traktRepository: TraktRepository) : BaseViewModel() {

    val favoriteMoviesLoader = MutableLiveData<LoadingState>()
    val favoriteTvShowsLoader = MutableLiveData<LoadingState>()

    val favoriteNoMovies = MutableLiveData<Boolean>()
    val favoriteNoTvShows = MutableLiveData<Boolean>()

    private val _movieResult = MutableLiveData<List<GetSingleTitleResponse.Data>>()
    val movieResult: LiveData<List<GetSingleTitleResponse.Data>> = _movieResult

    private val _tvShowResult = MutableLiveData<List<GetSingleTitleResponse.Data>>()
    val tvShowResult: LiveData<List<GetSingleTitleResponse.Data>> = _tvShowResult

    fun onSingleTitlePressed(titleId: Int) {
        navigateToNewFragment(FavoritesFragmentDirections.actionFavoritesFragmentToSingleTitleFragmentNav(titleId))
    }

    fun onProfileButtonPressed() {
        navigateToNewFragment(FavoritesFragmentDirections.actionFavoritesFragmentToProfileFragment())
    }

    fun getFavTitlesFromFirestore() {
        favoriteMoviesLoader.value = LoadingState.LOADING
        favoriteTvShowsLoader.value = LoadingState.LOADING
        favoritesRepository.getFavTitlesFromFirestore(currentUser()!!.uid, object :
            FavoritesCallBack {
            override fun moviesList(movies: MutableList<Int>) {
                if (movies.isNullOrEmpty()) {
                    favoriteMoviesLoader.value = LoadingState.LOADED
                    favoriteNoMovies.value = true
                } else {
                    favoriteNoMovies.value = false
                    val fetchMovieResult: MutableList<GetSingleTitleResponse.Data> = ArrayList()
                    viewModelScope.launch {
                        movies.forEach {
                            when (val moviesData = favoritesRepository.getSingleTitleData(it)) {
                                is Result.Success -> {
                                    val data = moviesData.data.data
                                    fetchMovieResult.add(data)

                                    _movieResult.value = fetchMovieResult
                                    favoriteMoviesLoader.value = LoadingState.LOADED
                                }
                            }
                        }
                    }
                }
            }

            override fun tvShowsList(tvShows: MutableList<Int>) {
                if (tvShows.isNullOrEmpty()) {
                    favoriteTvShowsLoader.value = LoadingState.LOADED
                    favoriteNoTvShows.value = true
                } else {
                    favoriteNoTvShows.value = false
                    val fetchTvShowResult: MutableList<GetSingleTitleResponse.Data> = ArrayList()
                    viewModelScope.launch {
                        tvShows.forEach {
                            when (val tvShowsData = favoritesRepository.getSingleTitleData(it)) {
                                is Result.Success -> {
                                    val data = tvShowsData.data.data
                                    fetchTvShowResult.add(data)

                                    _tvShowResult.value = fetchTvShowResult
                                    favoriteTvShowsLoader.value = LoadingState.LOADED
                                }
                            }
                        }
                    }
                }
            }

        })
    }

    fun removeFavTitleFromFirestore(titleId: Int) {
        viewModelScope.launch {
            val removeFromFavorites = favoritesRepository.removeFavTitleFromFirestore(currentUser()!!.uid, titleId)
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