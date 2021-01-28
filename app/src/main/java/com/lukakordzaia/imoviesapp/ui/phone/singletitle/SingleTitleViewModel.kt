package com.lukakordzaia.imoviesapp.ui.phone.singletitle

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.imoviesapp.database.ImoviesDatabase
import com.lukakordzaia.imoviesapp.database.WatchedDetails
import com.lukakordzaia.imoviesapp.datamodels.TitleData
import com.lukakordzaia.imoviesapp.datamodels.TitleDetails
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.repository.SingleTitleRepository
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class SingleTitleViewModel : BaseViewModel() {
    private val repository = SingleTitleRepository()

    private val _singleTitleData = MutableLiveData<TitleData.Data>()
    val singleTitleData: LiveData<TitleData.Data> = _singleTitleData

    private val _titleDetails = MutableLiveData<TitleDetails>()
    val titleDetails: LiveData<TitleDetails> = _titleDetails

    private val _titleIsInDb = MutableLiveData<Boolean>()
    val titleIsInDb: LiveData<Boolean> = _titleIsInDb

    fun onPlayPressed(titleId: Int, titleDetails: TitleDetails) {
        navigateToNewFragment(SingleTitleFragmentDirections.actionSingleTitleFragmentToChooseTitleDetailsFragment(
                titleId,
                titleDetails.numOfSeasons,
                titleDetails.isTvShow
        ))
    }

    fun onTrailerPressed(titleId: Int, isTvShow: Boolean, trailerURl: String?) {
        navigateToNewFragment(SingleTitleFragmentDirections.actionSingleTitleFragmentToVideoPlayerFragmentNav(
                0,
                0,
                titleId,
                isTvShow,
                "ENG",
                trailerUrl = trailerURl
        ))
    }

    fun getSingleTitleData(titleId: Int) {
        viewModelScope.launch {
            when (val data = repository.getSingleTitleData(titleId)) {
                is Result.Success -> {
                    _singleTitleData.value = data.data.data
                    checkTvShowAndFiles()
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

    fun titleIsInDb(exists: Boolean) {
        _titleIsInDb.value = exists
    }

    fun getSingleWatchedTitleDetails(context: Context, titleId: Int): LiveData<WatchedDetails> {
        val database = ImoviesDatabase.getDatabase(context)?.getDao()
        return repository.getSingleWatchedTitles(database!!, titleId)
    }

    fun deleteTitleFromDb(context: Context, titleId: Int) {
        val database = ImoviesDatabase.getDatabase(context)?.getDao()
        viewModelScope.launch {
            repository.deleteTitleFromDb(database!!, titleId)
        }
    }

    private fun checkTvShowAndFiles() {
        if (singleTitleData.value!!.isTvShow != false) {
            if (!singleTitleData.value!!.seasons!!.data.isNullOrEmpty()) {
                _titleDetails.value = TitleDetails(singleTitleData.value!!.seasons!!.data?.last()?.number!!, true)

            } else {
                _titleDetails.value = TitleDetails(0, true)
            }
        } else {
            _titleDetails.value = TitleDetails(0, false)
        }
    }
}