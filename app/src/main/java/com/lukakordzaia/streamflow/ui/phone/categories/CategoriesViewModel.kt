package com.lukakordzaia.streamflow.ui.phone.categories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.datamodels.GenreList
import com.lukakordzaia.streamflow.datamodels.StudioList
import com.lukakordzaia.streamflow.datamodels.TitleList
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.repository.CategoriesRepository
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class CategoriesViewModel(private val repository: CategoriesRepository) : BaseViewModel() {

    private val _allGenresList = MutableLiveData<List<GenreList.Data>>()
    val allGenresList: LiveData<List<GenreList.Data>> = _allGenresList

    private val _topStudioList = MutableLiveData<List<StudioList.Data>>()
    val topStudioList: LiveData<List<StudioList.Data>> = _topStudioList

    private val _topTrailerList = MutableLiveData<List<TitleList.Data>>()
    val topTrailerList: LiveData<List<TitleList.Data>> = _topTrailerList

    fun onSingleGenrePressed(genreId: Int) {
        navigateToNewFragment(CategoriesFragmentDirections.actionCategoriesFragmentToSingleGenreFragment(genreId))
    }

    fun onSingleStudioPressed(studioId: Int) {
        navigateToNewFragment(CategoriesFragmentDirections.actionCategoriesFragmentToSingleStudioFragment(studioId))
    }

    fun onSingleTrailerPressed(titleId: Int, trailerUrl: String) {
        navigateToNewFragment(CategoriesFragmentDirections.actionCategoriesFragmentToVideoPlayerFragmentNav(
            titleId = titleId,
            chosenSeason = 0,
            chosenEpisode = 0,
            isTvShow = false,
            chosenLanguage = "ENG",
            trailerUrl = trailerUrl
        ))
    }

    fun getAllGenres() {
        viewModelScope.launch {
            when (val genres = repository.getAllGenres()) {
                is Result.Success -> {
                    _allGenresList.value = genres.data.data
                    setLoading(false)
                }
                is Result.Error -> {
                    Log.d("errorgenres", genres.exception)
                }
            }
        }
    }

    fun getTopStudios() {
        viewModelScope.launch {
            when (val studios = repository.getTopStudios()) {
                is Result.Success -> {
                    _topStudioList.value = studios.data.data
                    setLoading(false)
                }
                is Result.Error -> {
                    Log.d("errorgenres", studios.exception)
                }
            }
        }
    }

    fun getTopTrailers() {
        viewModelScope.launch {
            when (val trailers = repository.getTopTrailers()) {
                is Result.Success -> {
                    _topTrailerList.value = trailers.data.data
                    setLoading(false)
                }
                is Result.Error -> {
                    Log.d("errortrailers", trailers.exception)
                }
            }
        }
    }
}