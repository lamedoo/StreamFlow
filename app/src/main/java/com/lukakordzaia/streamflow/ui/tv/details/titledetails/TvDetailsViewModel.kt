package com.lukakordzaia.streamflow.ui.tv.details.titledetails

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.database.DbDetails
import com.lukakordzaia.streamflow.database.ImoviesDatabase
import com.lukakordzaia.streamflow.datamodels.*
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.repository.TvDetailsRepository
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class TvDetailsViewModel(private val repository: TvDetailsRepository) : BaseViewModel() {
    private val _singleTitleData = MutableLiveData<SingleTitleData.Data>()
    val singleSingleTitleData: LiveData<SingleTitleData.Data> = _singleTitleData

    private val _singleMovieFiles = MutableLiveData<TitleFiles>()

    private val _movieNotYetAdded = MutableLiveData<Boolean>()
    val movieNotYetAdded: LiveData<Boolean> = _movieNotYetAdded

    private val _loader = MutableLiveData<LoadingState>()
    val loader: LiveData<LoadingState> = _loader

    private val _availableLanguages = MutableLiveData<MutableList<String>>()
    val availableLanguages: LiveData<MutableList<String>> = _availableLanguages

    private val _chosenLanguage = MutableLiveData<String>("ENG")
    val chosenLanguage: LiveData<String> = _chosenLanguage

    private val _chosenSeason = MutableLiveData<Int>(0)
    val chosenSeason: LiveData<Int> = _chosenSeason

    private val _chosenEpisode = MutableLiveData<Int>(0)
    val chosenEpisode: LiveData<Int> = _chosenEpisode

    private val _episodeNames = MutableLiveData<List<TitleEpisodes>>()
    val episodeNames: LiveData<List<TitleEpisodes>> = _episodeNames

    private val _numOfSeasons = MutableLiveData<Int>()
    val numOfSeasons: LiveData<Int> = _numOfSeasons

    private val _castData = MutableLiveData<List<TitleCast.Data>>()
    val castData: LiveData<List<TitleCast.Data>> = _castData

    private val _singleTitleRelated = MutableLiveData<List<TitleList.Data>>()
    val singleTitleRelated: LiveData<List<TitleList.Data>> = _singleTitleRelated

    fun getSingleTitleData(titleId: Int) {
        viewModelScope.launch {
            when (val data = repository.getSingleTitleData(titleId)) {
                is Result.Success -> {
                    _singleTitleData.value = data.data.data
                    if (data.data.data.seasons != null) {
                        _numOfSeasons.value = data.data.data.seasons.data.size
                    }
                    setLoading(false)
                }
                is Result.Error -> {
                    Log.d("errorsinglemovies", data.exception)
                }
            }
        }
    }

    fun checkTitleInDb(context: Context, titleId: Int): LiveData<Boolean> {
        val database = ImoviesDatabase.getDatabase(context)?.getDao()
        return repository.checkTitleInDb(database!!, titleId)
    }

    fun getSingleWatchedTitleDetails(context: Context, titleId: Int): LiveData<DbDetails> {
        val database = ImoviesDatabase.getDatabase(context)?.getDao()
        return repository.getSingleWatchedTitles(database!!, titleId)
    }

    fun deleteTitleFromDb(context: Context, titleId: Int) {
        val database = ImoviesDatabase.getDatabase(context)?.getDao()
        viewModelScope.launch {
            repository.deleteTitleFromDb(database!!, titleId)
        }
    }

    fun getSingleTitleFiles(movieId: Int) {
        viewModelScope.launch {
            _loader.value = LoadingState.LOADING
            when (val files = repository.getSingleTitleFiles(movieId)) {
                is Result.Success -> {
                    val data = files.data.data
                    if (data.isNotEmpty()) {
                        val languages: MutableList<String> = ArrayList()
                        _singleMovieFiles.value = files.data
                        data[0].files!!.forEach {
                            it.lang?.let { it1 -> languages.add(it1) }
                        }
                        _availableLanguages.value = languages

                        _movieNotYetAdded.value = false
                    } else {
                        _movieNotYetAdded.value = true
                    }
                    _loader.value = LoadingState.LOADED
                }
                is Result.Error -> {
                    Log.d("errorfiles", files.exception)
                }
            }
        }
    }

    fun getTitleLanguageFiles(language: String) {
        _chosenLanguage.value = language
    }

    fun getSeasonFiles(movieId: Int, season: Int) {
        _chosenSeason.value = season
        viewModelScope.launch {
            when (val files = repository.getSingleTitleFiles(movieId, season)) {
                is Result.Success -> {
                    val data = files.data.data

                    val getEpisodeNames: MutableList<TitleEpisodes> = ArrayList()
                    data.forEach {
                        getEpisodeNames.add(TitleEpisodes(it.episode, it.title, it.covers.x1050!!))
                    }
                    _episodeNames.value = getEpisodeNames
                    Log.d("episodenames", "${episodeNames.value}")

                    setLoading(false)
                }
                is Result.Error -> {
                    Log.d("errorseasons", files.exception)
                }
            }
        }
    }

    fun getEpisodeFile(episodeNum: Int) {
        _chosenEpisode.value = episodeNum
    }

    fun getSingleTitleCast(titleId: Int) {
        viewModelScope.launch {
            when (val cast = repository.getSingleTitleCast(titleId, "cast")) {
                is Result.Success -> {
                    val data = cast.data.data

                    _castData.value = data
                }
                is Result.Error -> {
                    Log.d("errorcast", cast.exception)
                }
            }
        }
    }

    fun getSingleTitleRelated(titleId: Int) {
        viewModelScope.launch {
            when (val related = repository.getSingleTitleRelated(titleId)) {
                is Result.Success -> {
                    val data = related.data.data
                    _singleTitleRelated.value = data
                }
            }
        }
    }
}