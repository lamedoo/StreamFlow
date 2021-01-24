package com.lukakordzaia.imoviesapp.ui.phone.genres.singlegenre

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

    private val _singleGenreList = MutableLiveData<MutableList<TitleList.Data>>()
    val singleGenreList: LiveData<MutableList<TitleList.Data>> = _singleGenreList

    private val fetchSingleGenreList: MutableList<TitleList.Data> = ArrayList()

    private val _hasMorePage = MutableLiveData(true)
    val hasMorePage: LiveData<Boolean> = _hasMorePage

    fun onSingleTitlePressed(titleId: Int) {
        navigateToNewFragment(SingleGenreFragmentDirections.actionSingleGenreFragmentToSingleTitleFragmentNav(titleId))
    }

    fun getSingleGenre(genreId: Int, page: Int) {
        viewModelScope.launch {
            when (val singleGenre = repository.getSingleGenre(genreId, page)) {
                is Result.Success -> {
                    singleGenre.data.data!!.forEach {
                        fetchSingleGenreList.add(it)
                    }
                    _singleGenreList.value = fetchSingleGenreList
                    _hasMorePage.value = singleGenre.data.meta.pagination.totalPages!! > singleGenre.data.meta.pagination.currentPage!!
                    setLoading(false)
                }
                is Result.Error -> {
                    Log.d("errorsinglegenre", singleGenre.exception)
                }
            }
        }
    }
}