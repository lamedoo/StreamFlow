package com.lukakordzaia.imoviesapp.ui.phone.genres.singlegenre

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.imoviesapp.network.Result
import com.lukakordzaia.imoviesapp.datamodels.TitleList
import com.lukakordzaia.imoviesapp.repository.GenresRepository
import com.lukakordzaia.imoviesapp.ui.baseclasses.BaseViewModel
import kotlinx.coroutines.launch

class SingleGenreViewModel : BaseViewModel() {
    private val repository = GenresRepository()

    private val _singleGenreList = MutableLiveData<List<TitleList.Data>>()
    val singleGenreList: LiveData<List<TitleList.Data>> = _singleGenreList

    private val fetchSingleGenreList: MutableList<TitleList.Data> = ArrayList()

    private val _singleGenreAnimation = MutableLiveData<List<TitleList.Data>>()
    val singleGenreAnimation: LiveData<List<TitleList.Data>> = _singleGenreAnimation

    private val _singleGenreComedy = MutableLiveData<List<TitleList.Data>>()
    val singleGenreComedy: LiveData<List<TitleList.Data>> = _singleGenreComedy

    private val _singleGenreMelodrama = MutableLiveData<List<TitleList.Data>>()
    val singleGenreMelodrama: LiveData<List<TitleList.Data>> = _singleGenreMelodrama

    private val _singleGenreHorror = MutableLiveData<List<TitleList.Data>>()
    val singleGenreHorror: LiveData<List<TitleList.Data>> = _singleGenreHorror

    private val _singleGenreAdventure = MutableLiveData<List<TitleList.Data>>()
    val singleGenreAdventure: LiveData<List<TitleList.Data>> = _singleGenreAdventure

    private val _singleGenreAction = MutableLiveData<List<TitleList.Data>>()
    val singleGenreAction: LiveData<List<TitleList.Data>> = _singleGenreAction

    private val _hasMorePage = MutableLiveData(true)
    val hasMorePage: LiveData<Boolean> = _hasMorePage

    fun onSingleTitlePressed(titleId: Int) {
        navigateToNewFragment(SingleGenreFragmentDirections.actionSingleGenreFragmentToSingleTitleFragmentNav(titleId))
    }

    fun getSingleGenre(genreId: Int, page: Int) {
        viewModelScope.launch {
            when (val singleGenre = repository.getSingleGenre(genreId, page)) {
                is Result.Success -> {
                    singleGenre.data.data.forEach {
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

    fun getSingleGenreForTv(genreId: Int, page: Int) {
        viewModelScope.launch {
            when (val singleGenre = repository.getSingleGenre(genreId, page)) {
                is Result.Success -> {
                    val data = singleGenre.data.data
                    when (genreId) {
                        265 -> _singleGenreAnimation.value = data
                        258 -> _singleGenreComedy.value = data
                        260 -> _singleGenreMelodrama.value = data
                        255 -> _singleGenreHorror.value = data
                        266 -> _singleGenreAdventure.value = data
                        248 -> _singleGenreAction.value = data
                    }
                }
                is Result.Error -> {
                    Log.d("errorsinglegenre", singleGenre.exception)
                }
            }
        }
    }
}

//დრამა, მიუზიკლი, მისტიკა, მძაფრ-სიუჟეტიანი, საოჯახო