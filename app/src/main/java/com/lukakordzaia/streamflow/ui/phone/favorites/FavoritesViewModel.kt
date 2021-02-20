package com.lukakordzaia.streamflow.ui.phone.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.datamodels.AddMovieToTraktList
import com.lukakordzaia.streamflow.datamodels.AddTvShowToTraktList
import com.lukakordzaia.streamflow.datamodels.GetTraktSfListByType
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.repository.FavoritesRepository
import com.lukakordzaia.streamflow.repository.TraktRepository
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class FavoritesViewModel(private val favoritesRepository: FavoritesRepository, private val traktRepository: TraktRepository) : BaseViewModel() {

    val favoriteMoviesLoader = MutableLiveData<LoadingState>()
    val favoriteTvShowsLoader = MutableLiveData<LoadingState>()

    val favoriteNoMovies = MutableLiveData<Boolean>()
    val favoriteNoTvShows = MutableLiveData<Boolean>()

    private val traktMoviesList = MutableLiveData<GetTraktSfListByType>()
    private val traktTvShowsList = MutableLiveData<GetTraktSfListByType>()

    private val fetchMovieSearchResult: MutableList<TitleList.Data> = ArrayList()
    private val _movieSearchResult = MutableLiveData<List<TitleList.Data>>()
    val movieSearchResult: LiveData<List<TitleList.Data>> = _movieSearchResult

    private val fetchTvShowSearchResult: MutableList<TitleList.Data> = ArrayList()
    private val _tvShowSearchResult = MutableLiveData<List<TitleList.Data>>()
    val tvShowSearchResult: LiveData<List<TitleList.Data>> = _tvShowSearchResult

    fun onSingleTitlePressed(titleId: Int) {
        navigateToNewFragment(FavoritesFragmentDirections.actionFavoritesFragmentToSingleTitleFragmentNav(titleId))
    }

    fun getTraktListByMovies(accessToken: String) {
        viewModelScope.launch {
            when (val traktMovies = traktRepository.getSfListByType("movie", accessToken)) {
                is Result.Success -> {
                    traktMoviesList.value = traktMovies.data

                    if (traktMoviesList.value.isNullOrEmpty()) {
                        favoriteMoviesLoader.value = LoadingState.LOADED
                        favoriteNoMovies.value = true
                    } else {
                        searchMoviesInImovies(traktMoviesList.value!!)
                    }

                }
            }
        }
    }

    private fun searchMoviesInImovies(traktMovieList: GetTraktSfListByType) {
        fetchMovieSearchResult.clear()
        favoriteMoviesLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            traktMovieList.forEach { traktMovie ->
                when (val search = favoritesRepository.getSearchFavoriteTitles(traktMovie.movie.title.toLowerCase(Locale.ROOT), 1, "${traktMovie.movie.year},${traktMovie.movie.year}")) {
                    is Result.Success -> {
                        val data = search.data.data

                        _movieSearchResult.value = data

                        data.forEach {
                            if (traktMovie.movie.title == it.secondaryName || traktMovie.movie.title == it.originalName && traktMovie.movie.year == it.year) {
                                fetchMovieSearchResult.add(it)
                            }
                        }

                        _movieSearchResult.value = fetchMovieSearchResult
                        favoriteMoviesLoader.value = LoadingState.LOADED
                    }
                }
            }
        }
    }

    fun getTraktListByTvShows(accessToken: String) {
        viewModelScope.launch {
            when (val traktTvShows = traktRepository.getSfListByType("show", accessToken)) {
                is Result.Success -> {
                    traktTvShowsList.value = traktTvShows.data

                    if (traktTvShowsList.value.isNullOrEmpty()) {
                        favoriteTvShowsLoader.value = LoadingState.LOADED
                        favoriteNoTvShows.value = true
                    } else {
                        searchTvShowsInImovies(traktTvShowsList.value!!)
                    }
                }
            }
        }
    }

    private fun searchTvShowsInImovies(traktTvShowList: GetTraktSfListByType) {
        fetchTvShowSearchResult.clear()
        favoriteTvShowsLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            traktTvShowList.forEach { traktMovie ->
                when (val search = favoritesRepository.getSearchFavoriteTitles(traktMovie.show.title.toLowerCase(Locale.ROOT), 1, "${traktMovie.show.year-1},${traktMovie.show.year}")) {
                    is Result.Success -> {
                        val data = search.data.data

                        _tvShowSearchResult.value = data

                        data.forEach {
                            if (traktMovie.show.title == it.secondaryName || traktMovie.show.title == it.originalName) {
                                fetchTvShowSearchResult.add(it)
                            }
                        }

                        _tvShowSearchResult.value = fetchTvShowSearchResult
                        favoriteTvShowsLoader.value = LoadingState.LOADED
                    }
                }
            }
        }
    }

    fun getSingleTitleImdb(titleId: Int, accessToken: String) {
        viewModelScope.launch {
            when (val title = favoritesRepository.getSingleTitleData(titleId)) {
                is Result.Success -> {
                    val data = title.data.data
                    val imdbId = data.imdbUrl!!.substring(27, data.imdbUrl.length)

                    if (data.isTvShow) {
                        removeTvShowFromTraktList(AddTvShowToTraktList(
                                tvShows = listOf(AddTvShowToTraktList.Showy(
                                        ids = AddTvShowToTraktList.Showy.Ids(
                                                imdb = imdbId
                                        )
                                ))
                        ), accessToken)
                    } else {
                        removeMovieFromTraktList(AddMovieToTraktList(
                                movies = listOf(AddMovieToTraktList.Movy(
                                        ids = AddMovieToTraktList.Movy.Ids(
                                                imdb = imdbId
                                        )
                                ))
                        ), accessToken)
                    }
                }
            }
        }
    }

    private fun removeMovieFromTraktList(movieFromTraktList: AddMovieToTraktList, accessToken: String) {
        viewModelScope.launch {
            when (val removeMovie = traktRepository.removeMovieFromTraktList(movieFromTraktList, accessToken)) {
                is Result.Success -> {
                    newToastMessage("ფილმი წაიშალა ფავორიტებიდან")
                }
            }
        }
    }

    private fun removeTvShowFromTraktList(tvShowFromTraktList: AddTvShowToTraktList, accessToken: String) {
        viewModelScope.launch {
            when (val removeTvShow = traktRepository.removeTvShowFromTraktList(tvShowFromTraktList, accessToken)) {
                is Result.Success -> {
                    newToastMessage("სერიალი წაიშალა ფავორიტებიდან")
                }
            }
        }
    }
}