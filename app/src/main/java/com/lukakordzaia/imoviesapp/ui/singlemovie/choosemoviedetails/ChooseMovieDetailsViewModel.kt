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

    private val _singleMovieFiles = MutableLiveData<MovieFiles.Data>()

    private val _movieNotYetAdded = MutableLiveData<String>()
    val movieNotYetAdded: LiveData<String> = _movieNotYetAdded

    private val _availableLanguages = MutableLiveData<MutableList<String>>()
    val availableLanguages: LiveData<MutableList<String>> = _availableLanguages

    private val _availableEpisodes = MutableLiveData<Int>()
    val availableEpisodes: LiveData<Int> = _availableEpisodes

    private val _movieFile = MutableLiveData<String>()
    val movieFile: LiveData<String> = _movieFile

    fun onPlayButtonPressed(mediaLink: String) {
        navigateToNewFragment(ChooseMovieDetailsFragmentDirections.actionChooseMovieDetailsFragmentToVideoPlayerFragment(mediaLink))
    }

    fun toastMessageForFile(message: String) {
        newToastMessage(message)
    }

    fun getSingleMovieFiles(movieId: Int) {
        viewModelScope.launch {
            when (val files = repository.getSingleMovieFiles(movieId)) {
                is Result.Success -> {
                    if (files.data.data.isNotEmpty()) {
                        val languages: MutableList<String> = ArrayList()
                        _singleMovieFiles.value = files.data.data[0]
                        files.data.data[0].files.forEach {
                            languages.add(it.lang)
                        }
                        _availableLanguages.value = languages
                        Log.d("languages", languages.toString())
                    } else {
                        _movieNotYetAdded.value = "ფილმი/სერიალი მალე დაემატება"
                    }
                }
                is Result.Error -> {
                    Log.d("error1", files.exception)
                }
            }
        }
    }

    fun getLanguageFiles(language: String) {
        _singleMovieFiles.value!!.files.forEach { files ->
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
    }

    fun getSeasonFiles(movieId: Int, season: Int) {
        viewModelScope.launch {
            when (val files = repository.getSingleMovieFiles(movieId, season)) {
                is Result.Success -> {
                    if (files.data.data.isNotEmpty()) {
                            _availableEpisodes.value = files.data.data.size
                    }
                    Log.d("episodecount", files.data.data.size.toString())
                }
                is Result.Error -> {
                    Log.d("error2", files.exception)
                }
            }
        }
    }
}