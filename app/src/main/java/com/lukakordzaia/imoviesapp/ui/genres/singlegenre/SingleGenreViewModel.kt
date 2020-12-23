package com.lukakordzaia.imoviesapp.ui.genres.singlegenre

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.datamodels.TitleList
import com.lukakordzaia.imoviesapp.repository.GenresRepository
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class SingleGenreViewModel : BaseViewModel() {
    private val repository = GenresRepository()

    private val _singleGenreList = MutableLiveData<List<TitleList.Data>>()
    val singleGenreList: LiveData<List<TitleList.Data>> = _singleGenreList

    fun onSingleTitlePressed(titleId: Int) {
        navigateToNewFragment(SingleGenreFragmentDirections.actionSingleGenreFragmentToSingleTitleFragmentNav(titleId))
    }

    fun getSingleGenre(genreId: Int) {
        viewModelScope.launch {
            when (val singleGenre = repository.getSingleGenre(genreId)) {
                is Result.Success -> {
                    _singleGenreList.value = singleGenre.data.data
                }
                is Result.Error -> {
                    Log.d("errorsinglegenre", singleGenre.exception)
                }
            }
        }
    }
}