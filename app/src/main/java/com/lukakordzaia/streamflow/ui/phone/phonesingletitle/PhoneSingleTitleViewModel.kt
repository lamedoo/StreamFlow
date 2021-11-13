package com.lukakordzaia.streamflow.ui.phone.phonesingletitle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.core.datamodels.SingleTitleModel
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.Result
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleCastResponse
import com.lukakordzaia.core.network.toSingleTitleModel
import com.lukakordzaia.core.network.toTitleListModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PhoneSingleTitleViewModel : BaseViewModel() {
    val favoriteLoader = MutableLiveData<LoadingState>()

    private val _singleTitleData = MutableLiveData<SingleTitleModel>()
    val getSingleTitleResponse: LiveData<SingleTitleModel> = _singleTitleData

    private val _castData = MutableLiveData<List<GetSingleTitleCastResponse.Data>>()
    val castResponseDataGetSingle: LiveData<List<GetSingleTitleCastResponse.Data>> = _castData

    private val _titleGenres = MutableLiveData<List<String>>()
    val titleGenres: LiveData<List<String>> = _titleGenres

    private val _titleDirector = MutableLiveData<GetSingleTitleCastResponse.Data>()
    val titleDirector: LiveData<GetSingleTitleCastResponse.Data> = _titleDirector

    private val _singleTitleRelated = MutableLiveData<List<SingleTitleModel>>()
    val singleTitleRelated: LiveData<List<SingleTitleModel>> = _singleTitleRelated

    private val _addToFavorites = MutableLiveData<Boolean>()
    val addToFavorites: LiveData<Boolean> = _addToFavorites

    private val _continueWatchingDetails = MediatorLiveData<ContinueWatchingRoom?>()
    val continueWatchingDetails: LiveData<ContinueWatchingRoom?> = _continueWatchingDetails

    fun onEpisodesPressed(titleId: Int, titleName: String, seasonNum: Int) {
        navigateToNewFragment(
                PhoneSingleTitleFragmentDirections.actionSingleTitleFragmentToChooseTitleDetailsFragment(
                    titleId,
                    titleName,
                    seasonNum
                )
        )
    }

    fun onRelatedTitlePressed(titleId: Int) {
        navigateToNewFragment(PhoneSingleTitleFragmentDirections.actionSingleTitleFragmentSelf(titleId))
    }

    private suspend fun getSingleTitleData(titleId: Int) {
        val fetchTitleGenres: MutableList<String> = ArrayList()
        when (val titleData = environment.singleTitleRepository.getSingleTitleData(titleId)) {
            is Result.Success -> {
                val data = titleData.data
                _singleTitleData.postValue(data.toSingleTitleModel())

                if (sharedPreferences.getLoginToken() == "") {
                    getSingleContinueWatchingFromRoom(titleId)
                } else {
                    if (data.data.userWatch?.data?.season != null) {
                        _continueWatchingDetails.postValue(ContinueWatchingRoom(
                            titleId = titleId,
                            language = data.data.userWatch!!.data?.language!!,
                            watchedDuration = data.data.userWatch!!.data?.progress!!,
                            titleDuration = data.data.userWatch!!.data?.duration!!,
                            isTvShow = data.data.isTvShow,
                            season = data.data.userWatch!!.data?.season!!,
                            episode = data.data.userWatch!!.data?.episode!!
                        ))
                    } else {
                        _continueWatchingDetails.postValue(null)
                    }
                }

                _addToFavorites.postValue(data.data.userWantsToWatch?.data?.status ?: false)

                data.data.genres.data.forEach {
                    fetchTitleGenres.add(it.primaryName!!)
                }
                _titleGenres.postValue(fetchTitleGenres)
            }
            is Result.Error -> {
                newToastMessage("ინფორმაცია - ${titleData.exception}")
            }
            is Result.Internet -> {
                setNoInternet()
            }
        }
    }

    private suspend fun getTitleCast(titleId: Int) {
        when (val cast = environment.singleTitleRepository.getSingleTitleCast(titleId, "cast")) {
            is Result.Success -> {
                val data = cast.data.data
                _castData.postValue(data)
            }
            is Result.Error -> {
                newToastMessage("მსახიობები - ${cast.exception}")
            }
            is Result.Internet -> {
                setNoInternet()
            }
        }
    }

    private suspend fun getTitleDirector(titleId: Int) {
        when (val cast = environment.singleTitleRepository.getSingleTitleCast(titleId, "director")) {
            is Result.Success -> {
                val data = cast.data.data

                if (!data.isNullOrEmpty()) {
                    _titleDirector.postValue(data[0])
                }
            }
            is Result.Error -> {
                newToastMessage("დირექტორი - ${cast.exception}")
            }
            is Result.Internet -> {
                setNoInternet()
            }
        }
    }

    private suspend fun getRelatedTitles(titleId: Int) {
        when (val related = environment.singleTitleRepository.getSingleTitleRelated(titleId)) {
            is Result.Success -> {
                val data = related.data.data
                _singleTitleRelated.postValue(data.toTitleListModel())
            }
            is Result.Error -> {
                newToastMessage("მსგავსი - ${related.exception}")
            }
            is Result.Internet -> {
                setNoInternet()
            }
        }
    }

    private fun getSingleContinueWatchingFromRoom(titleId: Int) {
        val data = environment.databaseRepository.getSingleContinueWatchingFromRoom(titleId)

        _continueWatchingDetails.addSource(data) {
            _continueWatchingDetails.value = it
        }
    }

    fun addWatchlistTitle(id: Int) {
        favoriteLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            when (val delete = environment.watchlistRepository.addWatchlistTitle(id)) {
                is Result.Success -> {
                    newToastMessage("ფილმი დაემატა ფავორიტებში")
                    _addToFavorites.value = true
                    favoriteLoader.value = LoadingState.LOADED
                }
            }
        }
    }

    fun deleteWatchlistTitle(id: Int) {
        favoriteLoader.value = LoadingState.LOADING
        viewModelScope.launch {
            when (val delete = environment.watchlistRepository.deleteWatchlistTitle(id)) {
                is Result.Success -> {
                    _addToFavorites.value = false
                    favoriteLoader.value = LoadingState.LOADED
                    newToastMessage("წარმატებით წაიშალა ფავორიტებიდან")
                }
            }
        }
    }

    fun fetchContent(titleId: Int) {
        setGeneralLoader(LoadingState.LOADING)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val getData = viewModelScope.launch {
                    getSingleTitleData(titleId)
                    getTitleCast(titleId)
                    getTitleDirector(titleId)
                    getRelatedTitles(titleId)
                }
                getData.join()
                setGeneralLoader(LoadingState.LOADED)
            }
        }
    }
}











