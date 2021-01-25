package com.lukakordzaia.imoviesapp.ui.phone.searchtitles

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.datamodels.TitleList
import com.lukakordzaia.imoviesapp.repository.SearchTitleRepository
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class SearchTitlesViewModel : BaseViewModel() {
    private val repository = SearchTitleRepository()

    private val _searchList = MutableLiveData<List<TitleList.Data>>()
    val searchList: LiveData<List<TitleList.Data>> = _searchList

    private val fetchSearchTitleList: MutableList<TitleList.Data> = ArrayList()

    fun onSingleTitlePressed(titleId: Int) {
        navigateToNewFragment(SearchTitlesFragmentDirections.actionSearchTitlesFragmentToSingleTitleFragmentNav(titleId))
    }

    fun getSearchTitles(keywords: String, page: Int) {
        viewModelScope.launch {
            when (val movies = repository.getSearchTitles(keywords, page)) {
                is Result.Success -> {
                    val data = movies.data.data
                    data!!.forEach {
                        fetchSearchTitleList.add(it)
                    }
                    _searchList.value = fetchSearchTitleList
                }
                is Result.Error -> {
                    Log.d("errornewmovies", movies.exception)
                }
            }
        }
    }
}