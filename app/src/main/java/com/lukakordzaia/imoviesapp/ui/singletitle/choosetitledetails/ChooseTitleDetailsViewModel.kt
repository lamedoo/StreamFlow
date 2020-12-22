package com.lukakordzaia.imoviesapp.ui.singletitle.choosetitledetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.datamodels.TitleFiles
import com.lukakordzaia.imoviesapp.repository.TitleFilesRepository
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class ChooseTitleDetailsViewModel : BaseViewModel() {
    private val repository = TitleFilesRepository()

    private val _singleMovieFiles = MutableLiveData<TitleFiles>()

    private val _movieNotYetAdded = MutableLiveData<Boolean>()
    val movieNotYetAdded: LiveData<Boolean> = _movieNotYetAdded

    private val _availableLanguages = MutableLiveData<MutableList<String>>()
    val availableLanguages: LiveData<MutableList<String>> = _availableLanguages

    private val _availableEpisodes = MutableLiveData<Int>()
    val availableEpisodes: LiveData<Int> = _availableEpisodes

    private val chosenLanguage = MutableLiveData<String>()

    private val chosenSeason = MutableLiveData<Int>(0)

    private val _chosenEpisode = MutableLiveData<Int>(0)
    private val chosenEpisode: LiveData<Int> = _chosenEpisode

    private val _movieFile = MutableLiveData<String>()
    val movieFile: LiveData<String> = _movieFile

    fun onPlayButtonPressed(mediaLink: String, titleId: Int, isTvShow: Boolean) {
        navigateToNewFragment(
                ChooseTitleDetailsFragmentDirections.actionChooseTitleDetailsFragmentToVideoPlayerFragmentNav(
                        mediaLink,
                        chosenSeason.value!!,
                        chosenEpisode.value!!,
                        titleId,
                        isTvShow,
                        chosenLanguage.value!!
                ),
        )
    }

    fun getSingleTitleFiles(movieId: Int) {
        viewModelScope.launch {
            when (val files = repository.getSingleTitleFiles(movieId)) {
                is Result.Success -> {
                    if (files.data.data.isNotEmpty()) {
                        val languages: MutableList<String> = ArrayList()
                        _singleMovieFiles.value = files.data
                        files.data.data[0].files!!.forEach {
                            it.lang?.let { it1 -> languages.add(it1) }
                        }
                        _availableLanguages.value = languages
                    } else {
                        _movieNotYetAdded.value = true
                    }
                }
                is Result.Error -> {
                    Log.d("errorfiles", files.exception)
                }
            }
        }
    }

    fun getTitleLanguageFiles(language: String) {
        _singleMovieFiles.value!!.data[0].files!!.forEach { files ->
            if (files.lang == language) {
                files.files.forEach {
                    if (it.quality == "HIGH") {
                        _movieFile.value = it.src
                    } else if (it.quality == "MEDIUM") {
                        _movieFile.value = it.src
                    }
                }
            }
        }
        chosenLanguage.value = language
    }

    fun getSeasonFiles(movieId: Int, season: Int) {
        chosenSeason.value = season
        viewModelScope.launch {
            when (val files = repository.getSingleTitleFiles(movieId, season)) {
                is Result.Success -> {
                    _availableEpisodes.value = files.data.data.size
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
}