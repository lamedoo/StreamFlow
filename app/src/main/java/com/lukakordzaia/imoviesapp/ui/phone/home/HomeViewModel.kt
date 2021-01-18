package com.lukakordzaia.imoviesapp.ui.phone.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.imoviesapp.database.ImoviesDatabase
import com.lukakordzaia.imoviesapp.database.WatchedDetails
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.datamodels.TitleList
import com.lukakordzaia.imoviesapp.network.datamodels.WatchedTitleData
import com.lukakordzaia.imoviesapp.repository.HomeRepository
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class HomeViewModel : BaseViewModel() {
    private val repository = HomeRepository()

    private val _movieList = MutableLiveData<List<TitleList.Data>>()
    val movieList: LiveData<List<TitleList.Data>> = _movieList

    private val _tvShowList = MutableLiveData<List<TitleList.Data>>()
    val tvShowList: LiveData<List<TitleList.Data>> = _tvShowList

    private val _watchedList = MutableLiveData<List<WatchedTitleData>>()
    val watchedList: LiveData<List<WatchedTitleData>> = _watchedList

    fun onSingleTitlePressed(titleId: Int) {
        navigateToNewFragment(HomeFragmentDirections.actionHomeFragmentToSingleTitleFragmentNav(titleId))
    }

    fun onWatchedTitlePressed(watchedTitleData: WatchedTitleData) {
        navigateToNewFragment(HomeFragmentDirections.actionHomeFragmentToVideoPlayerFragmentNav(
            titleId = watchedTitleData.id,
            chosenSeason = watchedTitleData.season,
            chosenEpisode = watchedTitleData.episode,
            isTvShow = watchedTitleData.isTvShow,
            watchedTime = watchedTitleData.watchedTime,
            chosenLanguage = watchedTitleData.language
        ))
    }

    fun getWatchedFromDb(context: Context): LiveData<List<WatchedDetails>> {
        val database = ImoviesDatabase.getDatabase(context)?.getDao()
        return repository.getWatchedFromDb(database!!)
    }

    fun getWatchedTitles(watchedDetails: List<WatchedDetails>) {
        val watchedTitles: MutableList<WatchedTitleData> = mutableListOf()

        viewModelScope.launch {
            watchedDetails.map {
                async {
                    when (val watched = repository.getSingleTitleData(it.titleId)) {
                        is Result.Success -> {
                            val data = watched.data.data
                            watchedTitles.add(WatchedTitleData(
                                    data.covers!!.data!!.x510,
                                    data.duration,
                                    data.id!!,
                                    data.isTvShow!!,
                                    data.primaryName,
                                    data.originalName,
                                    it.watchedTime,
                                    it.season,
                                    it.episode,
                                    it.language
                            ))
                            setLoading(false)
                        }
                    }
                    _watchedList.value = watchedTitles
                }
            }.awaitAll()
        }

//        watchedDetails.forEach {
//            viewModelScope.launch {
//                when (val watched = repository.getSingleTitleData(it.titleId)) {
//                    is Result.Success -> {
//                        val data = watched.data.data
//                        watchedTitles.add(WatchedTitleData(
//                                data.covers!!.data!!.x510,
//                                data.duration,
//                                data.id!!,
//                                data.isTvShow!!,
//                                data.primaryName,
//                                data.originalName,
//                                it.watchedTime,
//                                it.season,
//                                it.episode,
//                                it.language
//                        ))
//                        setLoading(false)
//                    }
//                }
//                _watchedList.value = watchedTitles
//            }
//        }
    }

    fun getTopMovies() {
        viewModelScope.launch {
            when (val movies = repository.getTopMovies()) {
                is Result.Success -> {
                    val data = movies.data.data
                    _movieList.value = data
                    setLoading(false)
                }
                is Result.Error -> {
                    Log.d("errornewmovies", movies.exception)
                }
            }
        }
    }

    fun getTopTvShows() {
        viewModelScope.launch {
            when (val tvShows = repository.getTopTvShows()) {
                is Result.Success -> {
                    val data = tvShows.data.data
                    _tvShowList.value = data
                    setLoading(false)
                }
                is Result.Error -> {
                    Log.d("errornewtvshows", tvShows.exception)
                }
            }
        }
    }
}