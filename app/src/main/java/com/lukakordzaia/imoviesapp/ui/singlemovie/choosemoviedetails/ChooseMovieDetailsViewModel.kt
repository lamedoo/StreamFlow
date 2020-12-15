package com.lukakordzaia.imoviesapp.ui.singlemovie.choosemoviedetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.models.MovieFiles
import com.lukakordzaia.imoviesapp.repository.MovieFilesRepository
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class ChooseMovieDetailsViewModel : BaseViewModel() {
    private val repository = MovieFilesRepository()

    private val _singleMovieFiles = MutableLiveData<MovieFiles>()

    private val _movieNotYetAdded = MutableLiveData<String>()
    val movieNotYetAdded: LiveData<String> = _movieNotYetAdded

    private val _availableLanguages = MutableLiveData<MutableList<String>>()
    val availableLanguages: LiveData<MutableList<String>> = _availableLanguages

    private val _availableEpisodes = MutableLiveData<Int>()
    val availableEpisodes: LiveData<Int> = _availableEpisodes

    private val _chosenLanguage = MutableLiveData<String>()
    val chosenLanguage: LiveData<String> = _chosenLanguage

    private val _chosenSeason = MutableLiveData<Int>(0)
    val chosenSeason: LiveData<Int> = _chosenSeason

    private val _chosenEpisode = MutableLiveData<Int>(0)
    private val chosenEpisode: LiveData<Int> = _chosenEpisode

    private val _movieFile = MutableLiveData<String>()
    val movieFile: LiveData<String> = _movieFile

    fun onPlayButtonPressed(mediaLink: String, movieId: Int, isTvShow: Boolean) {
        navigateToNewFragment(
                ChooseMovieDetailsFragmentDirections.actionChooseMovieDetailsFragmentToVideoPlayerFragment(
                        mediaLink,
                        chosenSeason.value!!,
                        chosenEpisode.value!!,
                        movieId,
                        isTvShow,
                        chosenLanguage.value!!
                ),
        )
    }

    fun getSingleTitleFiles(movieId: Int) {
        viewModelScope.launch {
            when (val files = repository.getSingleMovieFiles(movieId)) {
                is Result.Success -> {
                    if (files.data.data.isNotEmpty()) {
                        val languages: MutableList<String> = ArrayList()
                        _singleMovieFiles.value = files.data
                        files.data.data[0].files!!.forEach {
                            it.lang?.let { it1 -> languages.add(it1) }
                        }
                        _availableLanguages.value = languages
                    } else {
                        _movieNotYetAdded.value = "ფილმი/სერიალი მალე დაემატება"
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
                files.files!!.forEach {
                    if (it.quality == "HIGH") {
                        _movieFile.value = it.src
                    } else if (it.quality == "MEDIUM") {
                        _movieFile.value = it.src
                    }
                }
            }
        }
        _chosenLanguage.value = language
    }

    fun getSeasonFiles(movieId: Int, season: Int) {
        _chosenSeason.value = season
        viewModelScope.launch {
            when (val files = repository.getSingleMovieFiles(movieId, season)) {
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