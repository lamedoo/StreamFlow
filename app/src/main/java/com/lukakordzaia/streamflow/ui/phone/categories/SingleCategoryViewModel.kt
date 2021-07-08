package com.lukakordzaia.streamflow.ui.phone.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.network.models.response.titles.GetTitlesResponse
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.repository.CategoriesRepository
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.ui.phone.categories.singlegenre.SingleGenreFragmentDirections
import com.lukakordzaia.streamflow.ui.phone.categories.singlestudio.SingleStudioFragmentDirections
import com.lukakordzaia.streamflow.utils.AppConstants
import kotlinx.coroutines.launch

class SingleCategoryViewModel(private val repository: CategoriesRepository) : BaseViewModel() {
    private val _singleGenreList = MutableLiveData<List<GetTitlesResponse.Data>>()
    val singleGenreList: LiveData<List<GetTitlesResponse.Data>> = _singleGenreList

    private val fetchSingleGenreList: MutableList<GetTitlesResponse.Data> = ArrayList()

    private val _singleStudioList = MutableLiveData<List<GetTitlesResponse.Data>>()
    val singleStudioList: LiveData<List<GetTitlesResponse.Data>> = _singleStudioList

    private val fetchSingleStudioList: MutableList<GetTitlesResponse.Data> = ArrayList()

    private val _singleGenreAnimation = MutableLiveData<List<GetTitlesResponse.Data>>()
    val singleGenreAnimation: LiveData<List<GetTitlesResponse.Data>> = _singleGenreAnimation

    private val _singleGenreComedy = MutableLiveData<List<GetTitlesResponse.Data>>()
    val singleGenreComedy: LiveData<List<GetTitlesResponse.Data>> = _singleGenreComedy

    private val _singleGenreMelodrama = MutableLiveData<List<GetTitlesResponse.Data>>()
    val singleGenreMelodrama: LiveData<List<GetTitlesResponse.Data>> = _singleGenreMelodrama

    private val _singleGenreHorror = MutableLiveData<List<GetTitlesResponse.Data>>()
    val singleGenreHorror: LiveData<List<GetTitlesResponse.Data>> = _singleGenreHorror

    private val _singleGenreAdventure = MutableLiveData<List<GetTitlesResponse.Data>>()
    val singleGenreAdventure: LiveData<List<GetTitlesResponse.Data>> = _singleGenreAdventure

    private val _singleGenreAction = MutableLiveData<List<GetTitlesResponse.Data>>()
    val singleGenreAction: LiveData<List<GetTitlesResponse.Data>> = _singleGenreAction

    private val _hasMorePage = MutableLiveData(true)
    val hasMorePage: LiveData<Boolean> = _hasMorePage

    val categoryLoader = MutableLiveData<LoadingState>()

    fun onSingleTitlePressed(titleId: Int, start: Int) {
        when (start) {
            AppConstants.NAV_GENRE_TO_SINGLE -> navigateToNewFragment(SingleGenreFragmentDirections.actionSingleGenreFragmentToSingleTitleFragmentNav(titleId))
            AppConstants.NAV_STUDIO_TO_SINGLE -> navigateToNewFragment(SingleStudioFragmentDirections.actionSingleStudioFragmentToSingleTitleFragmentNav(titleId))
        }
    }

    fun getSingleGenre(genreId: Int, page: Int) {
        viewModelScope.launch {
            categoryLoader.value = LoadingState.LOADING
            when (val singleGenre = repository.getSingleGenre(genreId, page)) {
                is Result.Success -> {
                    fetchSingleGenreList.addAll(singleGenre.data.data)
                    _singleGenreList.value = fetchSingleGenreList
                    _hasMorePage.value = singleGenre.data.meta.pagination.totalPages!! > singleGenre.data.meta.pagination.currentPage!!
                    categoryLoader.value = LoadingState.LOADED
                }
                is Result.Error -> {
                    newToastMessage("ჟანრი - ${singleGenre.exception}")
                }
                is Result.Internet -> {
                    setNoInternet()
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
                    newToastMessage("ჟანრი - ${singleGenre.exception}")
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
        }
    }

    fun getSingleStudio(studioId: Int, page: Int) {
        viewModelScope.launch {
            categoryLoader.value = LoadingState.LOADING
            when (val singleStudio = repository.getSingleStudio(studioId, page)) {
                is Result.Success -> {
                    fetchSingleStudioList.addAll(singleStudio.data.data)
                    _singleStudioList.value = fetchSingleStudioList
                    _hasMorePage.value = singleStudio.data.meta.pagination.totalPages!! > singleStudio.data.meta.pagination.currentPage!!
                    categoryLoader.value = LoadingState.LOADED
                }
                is Result.Error -> {
                    newToastMessage("სტუდია - ${singleStudio.exception}")
                }
                is Result.Internet -> {
                    setNoInternet()
                }
            }
        }
    }
}