package com.lukakordzaia.streamflow.ui.phone.singletitle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.datamodels.*
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.repository.SingleTitleRepository
import com.lukakordzaia.streamflow.repository.TraktRepository
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SingleTitleViewModel(private val repository: SingleTitleRepository, private val traktRepo: TraktRepository) : BaseViewModel() {
    val singleTitleLoader = MutableLiveData<LoadingState>()
    val traktFavoriteLoader = MutableLiveData<LoadingState>()

    private val _singleTitleData = MutableLiveData<SingleTitleData.Data>()
    val singleTitleData: LiveData<SingleTitleData.Data> = _singleTitleData

    private val numOfSeasons = MutableLiveData<Int>()

    private val _castData = MutableLiveData<List<TitleCast.Data>>()
    val castData: LiveData<List<TitleCast.Data>> = _castData

    private val _titleGenres = MutableLiveData<List<String>>()
    val titleGenres: LiveData<List<String>> = _titleGenres
    private val fetchTitleGenres: MutableList<String> = ArrayList()

    private val _titleDirector = MutableLiveData<TitleCast.Data>()
    val titleDirector: LiveData<TitleCast.Data> = _titleDirector

    private val _singleTitleRelated = MutableLiveData<List<TitleList.Data>>()
    val singleTitleRelated: LiveData<List<TitleList.Data>> = _singleTitleRelated

    private val _addToFavorites = MutableLiveData<Boolean>()
    val addToFavorites: LiveData<Boolean> = _addToFavorites

    private val _imdbId = MutableLiveData<String>()
    val imdbId: LiveData<String> = _imdbId

    fun onPlayButtonPressed(titleId: Int) {
        navigateToNewFragment(
            SingleTitleFragmentDirections.actionSingleTitleFragmentToChooseTitleDetailsFragment(
                titleId,
                numOfSeasons.value!!,
                singleTitleData.value!!.isTvShow
            )
        )
    }

    fun onTrailerPressed(titleId: Int, isTvShow: Boolean, trailerURl: String?) {
        navigateToNewFragment(
            SingleTitleFragmentDirections.actionSingleTitleFragmentToVideoPlayerFragmentNav(
                    VideoPlayerData(
                            titleId,
                            isTvShow,
                            0,
                            "ENG",
                            0,
                            0L,
                            trailerURl
                    )
            )
        )
    }

    fun onRelatedTitlePressed(titleId: Int) {
        navigateToNewFragment(SingleTitleFragmentDirections.actionSingleTitleFragmentSelf(titleId))
    }

    fun getSingleTitleData(titleId: Int, accessToken: String) {
        viewModelScope.launch {
            singleTitleLoader.value = LoadingState.LOADING
            coroutineScope {
                launch {
                    when (val titleData = repository.getSingleTitleData(titleId)) {
                        is Result.Success -> {
                            val data = titleData.data.data
                            _singleTitleData.value = data
                            _imdbId.value = data.imdbUrl!!.substring(27, data.imdbUrl.length)

                            if (data.isTvShow) {
                                checkTitleInTraktList("show", accessToken)
                            } else {
                                checkTitleInTraktList("movie", accessToken)
                            }

                            if (data.seasons != null) {
                                if (data.seasons.data.isNotEmpty()) {
                                    numOfSeasons.value = data.seasons.data.size
                                } else {
                                    numOfSeasons.value = 0
                                }
                            } else {
                                numOfSeasons.value = 0
                            }

                            data.genres.data.forEach {
                                fetchTitleGenres.add(it.primaryName!!)
                            }
                            _titleGenres.value = fetchTitleGenres

                            singleTitleLoader.value = LoadingState.LOADED
                        }
                        is Result.Error -> {
                            newToastMessage("ინფორმაცია - ${titleData.exception}")
                        }
                        is Result.Internet -> {
                            setNoInternet()
                        }
                    }
                }
                launch {
                    when (val cast = repository.getSingleTitleCast(titleId, "cast")) {
                        is Result.Success -> {
                            val data = cast.data.data

                            _castData.value = data
                            singleTitleLoader.value = LoadingState.LOADED
                        }
                        is Result.Error -> {
                            newToastMessage("მსახიობები - ${cast.exception}")
                        }
                        is Result.Internet -> {
                            setNoInternet()
                        }
                    }
                }
                launch {
                    when (val cast = repository.getSingleTitleCast(titleId, "director")) {
                        is Result.Success -> {
                            val data = cast.data.data

                            if (!data.isNullOrEmpty()) {
                                _titleDirector.value = data[0]
                            }
                            singleTitleLoader.value = LoadingState.LOADED
                        }
                        is Result.Error -> {
                            newToastMessage("დირექტორი - ${cast.exception}")
                        }
                        is Result.Internet -> {
                            setNoInternet()
                        }
                    }
                }
                launch {
                    when (val related = repository.getSingleTitleRelated(titleId)) {
                        is Result.Success -> {
                            val data = related.data.data

                            _singleTitleRelated.value = data
                            singleTitleLoader.value = LoadingState.LOADED
                        }
                        is Result.Error -> {
                            newToastMessage("მსგავსი - ${related.exception}")
                        }
                        is Result.Internet -> {
                            setNoInternet()
                        }
                    }
                }
            }
        }
    }

    fun checkTitleInTraktList(type: String, accessToken: String) {
        _addToFavorites.value = false
        traktFavoriteLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            when (val list = traktRepo.getSfListByType(type, accessToken)) {
                is Result.Success -> {
                    when (type) {
                        "movie" -> {
                            list.data.forEach {
                                if (imdbId.value == it.movie.ids.imdb) {
                                    _addToFavorites.value = true
                                }
                            }
                        }
                        "show" -> {
                            list.data.forEach {
                                _addToFavorites.value = imdbId.value == it.show.ids.imdb
                            }
                        }
                    }
                    traktFavoriteLoader.value = LoadingState.LOADED
                }
            }
        }
    }

    fun addToFavorites(accessToken: String) {
        if (singleTitleData.value!!.isTvShow) {
            addTvShowToTraktList(accessToken)
        } else {
            addMovieToTraktList(accessToken)
        }
    }

    private fun addMovieToTraktList(accessToken: String) {
        val movieToTraktList = AddMovieToTraktList(
                movies = listOf(AddMovieToTraktList.Movy(
                        ids = AddMovieToTraktList.Movy.Ids(
                                imdb = imdbId.value!!
                        )
                ))
        )
        traktFavoriteLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            when (val addToList = traktRepo.addMovieToTraktList(movieToTraktList, accessToken)) {
                is Result.Success -> {
                    newToastMessage("ფილმი დაემატა ფავორიტებში")
                    _addToFavorites.value = true
                    traktFavoriteLoader.value = LoadingState.LOADED
                }
            }
        }
    }

    private fun addTvShowToTraktList(accessToken: String) {
        val tvShowToTraktList = AddTvShowToTraktList(
                tvShows = listOf(AddTvShowToTraktList.Showy(
                        ids = AddTvShowToTraktList.Showy.Ids(
                                imdb = imdbId.value!!
                        )
                ))
        )
        traktFavoriteLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            when (val addToList = traktRepo.addTvShowToTraktList(tvShowToTraktList, accessToken)) {
                is Result.Success -> {
                    newToastMessage("სერიალი დაემატა ფავორიტებში")
                    _addToFavorites.value = true
                    traktFavoriteLoader.value = LoadingState.LOADED
                }
            }
        }
    }

    fun removeMovieFromTraktList(accessToken: String) {
        val movieFromTraktList = AddMovieToTraktList(
                movies = listOf(AddMovieToTraktList.Movy(
                        ids = AddMovieToTraktList.Movy.Ids(
                                imdb = imdbId.value!!
                        )
                ))
        )
        traktFavoriteLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            when (val removeFromList = traktRepo.removeMovieFromTraktList(movieFromTraktList, accessToken)) {
                is Result.Success -> {
                    newToastMessage("ფილმი წაიშალა ფავორიტებიდან")
                    _addToFavorites.value = false
                    traktFavoriteLoader.value = LoadingState.LOADED
                }
            }
        }
    }
}