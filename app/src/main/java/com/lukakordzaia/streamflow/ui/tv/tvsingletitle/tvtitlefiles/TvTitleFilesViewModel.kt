package com.lukakordzaia.streamflow.ui.tv.tvsingletitle.tvtitlefiles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.datamodels.TitleEpisodes
import com.lukakordzaia.streamflow.network.FirebaseContinueWatchingCallBack
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.models.imovies.response.singletitle.GetSingleTitleCastResponse
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.utils.toTitleListModel
import kotlinx.coroutines.launch

class TvTitleFilesViewModel : BaseViewModel() {
    private val _availableLanguages = MutableLiveData<MutableList<String>>()
    val availableLanguages: LiveData<MutableList<String>> = _availableLanguages

    private val _chosenLanguage = MutableLiveData<String>()
    val chosenLanguage: LiveData<String> = _chosenLanguage

    private val _chosenSeason = MutableLiveData<Int>()
    val chosenSeason: LiveData<Int> = _chosenSeason

    private val _chosenEpisode = MutableLiveData<Int>()
    val chosenEpisode: LiveData<Int> = _chosenEpisode

    private val _episodeNames = MutableLiveData<List<TitleEpisodes>>()
    val episodeNames: LiveData<List<TitleEpisodes>> = _episodeNames

    private val _numOfSeasons = MutableLiveData<Int>()
    val numOfSeasons: LiveData<Int> = _numOfSeasons

    private val _castData = MutableLiveData<List<GetSingleTitleCastResponse.Data>>()
    val castResponseDataGetSingle: LiveData<List<GetSingleTitleCastResponse.Data>> = _castData

    private val _singleTitleRelated = MutableLiveData<List<SingleTitleModel>>()
    val singleTitleRelated: LiveData<List<SingleTitleModel>> = _singleTitleRelated

    private val _continueWatchingDetails = MediatorLiveData<ContinueWatchingRoom?>()
    val continueWatchingDetails: LiveData<ContinueWatchingRoom?> = _continueWatchingDetails

    fun getSingleTitleData(titleId: Int) {
        viewModelScope.launch {
            when (val data = environment.singleTitleRepository.getSingleTitleData(titleId)) {
                is Result.Success -> {
                    if (data.data.data.seasons != null) {
                        _numOfSeasons.value = data.data.data.seasons.data.size
                    }
                }
                is Result.Error -> {
                    newToastMessage(data.exception)
                }
            }
        }
    }

    fun getEpisodeFile(episodeNum: Int) {
        _chosenEpisode.value = episodeNum
    }

    fun getSeasonFiles(movieId: Int, season: Int) {
        _chosenSeason.value = season
        viewModelScope.launch {
            when (val files = environment.singleTitleRepository.getSingleTitleFiles(movieId, season)) {
                is Result.Success -> {
                    val data = files.data.data

                    val fetchLanguages: MutableList<String> = ArrayList()
                    data[0].files.forEach {
                        fetchLanguages.add(it.lang)
                    }
                    _availableLanguages.value = fetchLanguages

                    val getEpisodeNames: MutableList<TitleEpisodes> = ArrayList()
                    data.forEach {
                        getEpisodeNames.add(TitleEpisodes(it.episode, it.title, it.covers.x1050!!))
                    }
                    _episodeNames.value = getEpisodeNames
                }
                is Result.Error -> {
                    newToastMessage("ეპიზოდები - ${files.exception}")
                }
            }
        }
    }

    fun getSingleTitleCast(titleId: Int) {
        viewModelScope.launch {
            when (val cast = environment.singleTitleRepository.getSingleTitleCast(titleId, "cast")) {
                is Result.Success -> {
                    val data = cast.data.data

                    _castData.value = data
                }
                is Result.Error -> {
                    newToastMessage("მსახიობები - ${cast.exception}")
                }
            }
        }
    }

    fun getSingleTitleRelated(titleId: Int) {
        viewModelScope.launch {
            when (val related = environment.singleTitleRepository.getSingleTitleRelated(titleId)) {
                is Result.Success -> {
                    val data = related.data.data
                    _singleTitleRelated.value = data.toTitleListModel()
                }
                is Result.Error -> {
                    newToastMessage("მსგავსი - ${related.exception}")
                }
            }
        }
    }

    fun checkAuthDatabase(titleId: Int) {
        if (currentUser() == null) {
            getSingleContinueWatchingFromRoom(titleId)
        } else {
            checkContinueWatchingInFirestore(titleId)
        }
    }

    private fun getSingleContinueWatchingFromRoom(titleId: Int){
        val data = environment.databaseRepository.getSingleContinueWatchingFromRoom(titleId)

        _continueWatchingDetails.addSource(data) {
            _continueWatchingDetails.value = it
        }
    }

    private fun checkContinueWatchingInFirestore(titleId: Int) {
        environment.databaseRepository.checkContinueWatchingInFirestore(currentUser()!!.uid, titleId, object : FirebaseContinueWatchingCallBack {
            override fun continueWatchingTitle(title: ContinueWatchingRoom?) {
                _continueWatchingDetails.value = title
            }

        })
    }
}