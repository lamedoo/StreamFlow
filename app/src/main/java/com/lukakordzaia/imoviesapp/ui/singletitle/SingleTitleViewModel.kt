package com.lukakordzaia.imoviesapp.ui.singletitle

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.datamodels.TitleData
import com.lukakordzaia.imoviesapp.network.datamodels.TitleDetails
import com.lukakordzaia.imoviesapp.repository.SingleTitleRepository
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class SingleTitleViewModel : BaseViewModel() {
    private val repository = SingleTitleRepository()

    private val _singleMovieFiles = MutableLiveData<TitleData.Data>()
    val singleTitleFiles: LiveData<TitleData.Data> = _singleMovieFiles

    private val _titleDetails = MutableLiveData<TitleDetails>()
    val titleDetails: LiveData<TitleDetails> = _titleDetails



    fun onPlayPressed(movieId: Int, titleDetails: TitleDetails) {
        navigateToNewFragment(SingleTitleFragmentDirections.actionSingleTitleFragmentToChooseTitleDetailsFragment(
                movieId,
                titleDetails.numOfSeasons,
                titleDetails.isTvShow
        ))
    }

    fun getSingleMovieFiles(movieId: Int) {
        viewModelScope.launch {
            when (val data = repository.getSingleTitleData(movieId)) {
                is Result.Success -> {
                    _singleMovieFiles.value = data.data.data
                    checkTvShowAndFiles()
                }
                is Result.Error -> {
                    Log.d("errorsinglemovies", data.exception)
                }
            }
        }
    }

    private fun checkTvShowAndFiles() {
        if (singleTitleFiles.value!!.isTvShow != false) {
            if (!singleTitleFiles.value!!.seasons!!.data.isNullOrEmpty()) {
                _titleDetails.value = TitleDetails(singleTitleFiles.value!!.seasons!!.data?.last()?.number!!, true)

            } else {
                _titleDetails.value = TitleDetails(0, true)
            }
        } else {
            _titleDetails.value = TitleDetails(0, false)
        }
    }
}