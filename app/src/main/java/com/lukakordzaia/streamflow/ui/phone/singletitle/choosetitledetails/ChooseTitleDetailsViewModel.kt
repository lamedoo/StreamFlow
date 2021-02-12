package com.lukakordzaia.streamflow.ui.phone.singletitle.choosetitledetails

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.database.DbDetails
import com.lukakordzaia.streamflow.database.ImoviesDatabase
import com.lukakordzaia.streamflow.datamodels.TitleEpisodes
import com.lukakordzaia.streamflow.datamodels.TitleFiles
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.repository.SingleTitleRepository
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class ChooseTitleDetailsViewModel(private val repository: SingleTitleRepository) : BaseViewModel() {
    private val _titleIsInDb = MutableLiveData<Boolean>()
    val titleIsInDb: LiveData<Boolean> = _titleIsInDb

    private val _singleMovieFiles = MutableLiveData<TitleFiles>()

    private val _movieNotYetAdded = MutableLiveData<Boolean>()
    val movieNotYetAdded: LiveData<Boolean> = _movieNotYetAdded

    private val _availableLanguages = MutableLiveData<MutableList<String>>()
    val availableLanguages: LiveData<MutableList<String>> = _availableLanguages

    private val _chosenLanguage = MutableLiveData<String>()
    val chosenLanguage: LiveData<String> = _chosenLanguage

    private val _chosenSeason = MutableLiveData<Int>(0)
    val chosenSeason: LiveData<Int> = _chosenSeason

    private val _chosenEpisode = MutableLiveData<Int>(0)
    val chosenEpisode: LiveData<Int> = _chosenEpisode

    private val _episodeNames = MutableLiveData<List<TitleEpisodes>>()
    val episodeNames: LiveData<List<TitleEpisodes>> = _episodeNames


    fun onPlayButtonPressed(titleId: Int, isTvShow: Boolean) {
        navigateToNewFragment(
            ChooseTitleDetailsFragmentDirections.actionChooseTitleDetailsFragmentToVideoPlayerFragmentNav(
                if (isTvShow) 1 else chosenSeason.value!!,
                if (isTvShow) 1 else chosenEpisode.value!!,
                titleId,
                isTvShow,
                chosenLanguage.value!!,
                trailerUrl = null
            ),
        )
    }

    fun onEpisodePressed(titleId: Int, isTvShow: Boolean) {
        navigateToNewFragment(
            ChooseTitleDetailsFragmentDirections.actionChooseTitleDetailsFragmentToVideoPlayerFragmentNav(
                chosenSeason.value!!,
                chosenEpisode.value!!,
                titleId,
                isTvShow,
                chosenLanguage.value!!,
                trailerUrl = null
            ),
        )
    }


    fun onContinueWatchingPressed(dbDetails: DbDetails) {
        navigateToNewFragment(
            ChooseTitleDetailsFragmentDirections.actionChooseTitleDetailsFragmentToVideoPlayerFragmentNav(
                titleId = dbDetails.titleId,
                chosenSeason = dbDetails.season,
                chosenEpisode = dbDetails.episode,
                isTvShow = dbDetails.isTvShow,
                watchedTime = dbDetails.watchedDuration,
                chosenLanguage = dbDetails.language,
                trailerUrl = null
            ))
    }

    fun checkTitleInDb(context: Context, titleId: Int): LiveData<Boolean> {
        val database = ImoviesDatabase.getDatabase(context)?.getDao()
        return repository.checkTitleInDb(database!!, titleId)
    }

    fun titleIsInDb(exists: Boolean) {
        _titleIsInDb.value = exists
    }

    fun getSingleWatchedTitleDetails(context: Context, titleId: Int): LiveData<DbDetails> {
        val database = ImoviesDatabase.getDatabase(context)?.getDao()
        return repository.getSingleWatchedTitles(database!!, titleId)
    }

    fun getSingleTitleFiles(titleId: Int) {
        viewModelScope.launch {
            when (val files = repository.getSingleTitleFiles(titleId)) {
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
                    }
                    setLoading(false)
                }
                is Result.Error -> {
                    when (files.exception) {
                        "404 - გაურკვეველი პრობლემა" -> {
                            _movieNotYetAdded.value = true
                            setLoading(false)
                        }
                    }
                    Log.d("errorfiles", files.exception)
                }
            }
        }
    }

    fun getTitleLanguageFiles(language: String) {
        _chosenLanguage.value = language
    }

    fun getEpisodeFile(episodeNum: Int) {
        _chosenEpisode.value = episodeNum
    }

    fun getSeasonFiles(titleId: Int, season: Int) {
        _chosenSeason.value = season
        viewModelScope.launch {
            when (val files = repository.getSingleTitleFiles(titleId, season)) {
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
}