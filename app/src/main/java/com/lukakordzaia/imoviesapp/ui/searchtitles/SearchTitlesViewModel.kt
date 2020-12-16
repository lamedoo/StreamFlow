package com.lukakordzaia.imoviesapp.ui.searchtitles

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.network.models.TitleList
import com.lukakordzaia.imoviesapp.repository.SearchTitleRepository
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class SearchTitlesViewModel : BaseViewModel() {
    private val repository = SearchTitleRepository()

    private val _titleList = MutableLiveData<List<TitleList.Data>>()
    val titleList: LiveData<List<TitleList.Data>> = _titleList

    fun onSingleTitlePressed(titleId: Int) {
        navigateToNewFragment(SearchTitlesFragmentDirections.actionSearchTitlesFragmentToSingleTitleFragmentNav(titleId))
    }

    fun displayToast(message: String) {
        newToastMessage(message)
    }

    fun getSearchTitles(keywords: String) {
        viewModelScope.launch {
            when (val movies = repository.getSearchTitles(keywords)) {
                is Result.Success -> {
                    val data = movies.data.data
                    _titleList.value = data
                }
                is Result.Error -> {
                    Log.d("errornewmovies", movies.exception)
                }
            }
        }
    }
}