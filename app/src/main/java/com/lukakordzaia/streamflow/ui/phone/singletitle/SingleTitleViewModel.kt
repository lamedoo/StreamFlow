package com.lukakordzaia.streamflow.ui.phone.singletitle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.datamodels.AddTitleToFirestore
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.helpers.MapTitleData
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleCastResponse
import com.lukakordzaia.streamflow.network.models.imovies.response.titles.GetTitlesResponse
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

    private val _singleTitleData = MutableLiveData<SingleTitleModel>()
    val getSingleTitleResponse: LiveData<SingleTitleModel> = _singleTitleData

    private val _castData = MutableLiveData<List<GetSingleTitleCastResponse.Data>>()
    val castResponseDataGetSingle: LiveData<List<GetSingleTitleCastResponse.Data>> = _castData

    private val _titleGenres = MutableLiveData<List<String>>()
    val titleGenres: LiveData<List<String>> = _titleGenres

    private val _titleDirector = MutableLiveData<GetSingleTitleCastResponse.Data>()
    val getSingleTitleDirectorResponse: LiveData<GetSingleTitleCastResponse.Data> = _titleDirector

    private val _singleTitleRelated = MutableLiveData<List<SingleTitleModel>>()
    val singleTitleRelated: LiveData<List<SingleTitleModel>> = _singleTitleRelated

    private val _addToFavorites = MutableLiveData<Boolean>()
    val addToFavorites: LiveData<Boolean> = _addToFavorites

    fun onEpisodesPressed(titleId: Int, titleName: String, seasonNum: Int) {
        navigateToNewFragment(
                SingleTitleFragmentDirections.actionSingleTitleFragmentToChooseTitleDetailsFragment(
                    titleId,
                    titleName,
                    seasonNum
                )
        )
    }

    fun onRelatedTitlePressed(titleId: Int) {
        navigateToNewFragment(SingleTitleFragmentDirections.actionSingleTitleFragmentSelf(titleId))
    }

    fun getSingleTitleData(titleId: Int, accessToken: String) {
        val fetchTitleGenres: MutableList<String> = ArrayList()
        viewModelScope.launch {
            singleTitleLoader.value = LoadingState.LOADING
            coroutineScope {
                launch {
                    when (val titleData = repository.getSingleTitleData(titleId)) {
                        is Result.Success -> {
                            val data = titleData.data.data
                            _singleTitleData.value = MapTitleData().single(data)

//                            if (data.isTvShow) {
//                                checkTitleInTraktList("show", accessToken)
//                            } else {
//                                checkTitleInTraktList("movie", accessToken)
//                            }

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
                            _singleTitleRelated.value = MapTitleData().list(data)

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

//    private fun checkTitleInTraktList(type: String, accessToken: String) {
//        _addToFavorites.value = false
//        traktFavoriteLoader.value = LoadingState.LOADING
//        viewModelScope.launch {
//            when (val list = traktRepo.getSfListByType(type, accessToken)) {
//                is Result.Success -> {
//                    when (type) {
//                        "movie" -> {
//                            list.data.forEach {
//                                if (imdbId.value == it.movie.ids.imdb) {
//                                    _addToFavorites.value = true
//                                }
//                            }
//                        }
//                        "show" -> {
//                            list.data.forEach {
//                                _addToFavorites.value = imdbId.value == it.show.ids.imdb
//                            }
//                        }
//                    }
//                    traktFavoriteLoader.value = LoadingState.LOADED
//                }
//            }
//        }
//    }
//
//    fun addToFavorites(accessToken: String) {
//        if (singleTitleData.value!!.isTvShow) {
//            addTvShowToTraktList(accessToken)
//        } else {
//            addMovieToTraktList(accessToken)
//        }
//    }
//
//    private fun addMovieToTraktList(accessToken: String) {
//        val movieToTraktList = AddMovieToTraktList(
//                movies = listOf(AddMovieToTraktList.Movy(
//                        ids = AddMovieToTraktList.Movy.Ids(
//                                imdb = imdbId.value!!
//                        )
//                ))
//        )
//        traktFavoriteLoader.value = LoadingState.LOADING
//        viewModelScope.launch {
//            when (val addToList = traktRepo.addMovieToTraktList(movieToTraktList, accessToken)) {
//                is Result.Success -> {
//                    newToastMessage("ფილმი დაემატა ფავორიტებში")
//                    _addToFavorites.value = true
//                    traktFavoriteLoader.value = LoadingState.LOADED
//                }
//            }
//        }
//    }
//
//    private fun addTvShowToTraktList(accessToken: String) {
//        val tvShowToTraktList = AddTvShowToTraktList(
//                tvShows = listOf(AddTvShowToTraktList.Showy(
//                        ids = AddTvShowToTraktList.Showy.Ids(
//                                imdb = imdbId.value!!
//                        )
//                ))
//        )
//        traktFavoriteLoader.value = LoadingState.LOADING
//        viewModelScope.launch {
//            when (val addToList = traktRepo.addTvShowToTraktList(tvShowToTraktList, accessToken)) {
//                is Result.Success -> {
//                    newToastMessage("სერიალი დაემატა ფავორიტებში")
//                    _addToFavorites.value = true
//                    traktFavoriteLoader.value = LoadingState.LOADED
//                }
//            }
//        }
//    }

//    fun removeMovieFromTraktList(accessToken: String) {
//        val movieFromTraktList = AddMovieToTraktList(
//                movies = listOf(AddMovieToTraktList.Movy(
//                        ids = AddMovieToTraktList.Movy.Ids(
//                                imdb = imdbId.value!!
//                        )
//                ))
//        )
//        traktFavoriteLoader.value = LoadingState.LOADING
//        viewModelScope.launch {
//            when (val removeFromList = traktRepo.removeMovieFromTraktList(movieFromTraktList, accessToken)) {
//                is Result.Success -> {
//                    newToastMessage("ფილმი წაიშალა ფავორიტებიდან")
//                    _addToFavorites.value = false
//                    traktFavoriteLoader.value = LoadingState.LOADED
//                }
//            }
//        }
//    }

    fun addTitleToFirestore(info: SingleTitleModel) {
        if (currentUser() != null) {
            traktFavoriteLoader.value = LoadingState.LOADING
            viewModelScope.launch {
                val addToFavorites = repository.addFavTitleToFirestore(currentUser()!!.uid, AddTitleToFirestore(
                    info.nameEng!!,
                    info.isTvShow,
                    info.id,
                    info.imdbId!!
                ))
                if (addToFavorites) {
                    newToastMessage("ფილმი დაემატა ფავორიტებში")
                    _addToFavorites.value = true
                    traktFavoriteLoader.value = LoadingState.LOADED
                } else {
                    newToastMessage("სამწუხაროდ ვერ მოხერხდა ფავორიტებში დამატება")
                    _addToFavorites.value = false
                    traktFavoriteLoader.value = LoadingState.LOADED
                }
            }
        } else {
            newToastMessage("ფავორიტებში დასამატებლად, გაიარეთ ავტორიზაცია")
        }
    }

    fun removeTitleFromFirestore(titleId: Int) {
        traktFavoriteLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            val removeFromFavorites = repository.removeFavTitleFromFirestore(currentUser()!!.uid, titleId)
            if (removeFromFavorites) {
                _addToFavorites.value = false
                traktFavoriteLoader.value = LoadingState.LOADED
                newToastMessage("წარმატებით წაიშალა ფავორიტებიდან")
            } else {
                _addToFavorites.value = true
                traktFavoriteLoader.value = LoadingState.LOADED
            }
        }
    }

    fun checkTitleInFirestore(titleId: Int) {
        if (currentUser() != null) {
            traktFavoriteLoader.value = LoadingState.LOADING
            viewModelScope.launch {
                val checkTitle = repository.checkTitleInFirestore(currentUser()!!.uid, titleId)
                _addToFavorites.value = checkTitle!!.data != null
                traktFavoriteLoader.value = LoadingState.LOADED
            }
        } else {
            _addToFavorites.value = false
        }
    }
}











