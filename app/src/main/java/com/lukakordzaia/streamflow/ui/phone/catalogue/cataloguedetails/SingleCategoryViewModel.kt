package com.lukakordzaia.streamflow.ui.phone.catalogue.cataloguedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.streamflow.datamodels.SingleTitleModel
import com.lukakordzaia.streamflow.network.LoadingState
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.ui.baseclasses.BaseViewModel
import com.lukakordzaia.streamflow.ui.phone.catalogue.cataloguedetails.singlegenre.SingleGenreFragmentDirections
import com.lukakordzaia.streamflow.ui.phone.catalogue.cataloguedetails.singlestudio.SingleStudioFragmentDirections
import com.lukakordzaia.streamflow.utils.AppConstants
import com.lukakordzaia.streamflow.utils.toTitleListModel
import kotlinx.coroutines.launch

class SingleCategoryViewModel : BaseViewModel() {
    private val fetchSingleGenreList: MutableList<SingleTitleModel> = ArrayList()
    private val _singleGenreList = MutableLiveData<List<SingleTitleModel>>()
    val singleGenreList: LiveData<List<SingleTitleModel>> = _singleGenreList

    private val fetchSingleStudioList: MutableList<SingleTitleModel> = ArrayList()
    private val _singleStudioList = MutableLiveData<List<SingleTitleModel>>()
    val singleStudioList: LiveData<List<SingleTitleModel>> = _singleStudioList

    private val _singleGenreAnimation = MutableLiveData<List<SingleTitleModel>>()
    val singleGenreAnimation: LiveData<List<SingleTitleModel>> = _singleGenreAnimation

    private val _singleGenreComedy = MutableLiveData<List<SingleTitleModel>>()
    val singleGenreComedy: LiveData<List<SingleTitleModel>> = _singleGenreComedy

    private val _singleGenreMelodrama = MutableLiveData<List<SingleTitleModel>>()
    val singleGenreMelodrama: LiveData<List<SingleTitleModel>> = _singleGenreMelodrama

    private val _singleGenreHorror = MutableLiveData<List<SingleTitleModel>>()
    val singleGenreHorror: LiveData<List<SingleTitleModel>> = _singleGenreHorror

    private val _singleGenreAdventure = MutableLiveData<List<SingleTitleModel>>()
    val singleGenreAdventure: LiveData<List<SingleTitleModel>> = _singleGenreAdventure

    private val _singleGenreAction = MutableLiveData<List<SingleTitleModel>>()
    val singleGenreAction: LiveData<List<SingleTitleModel>> = _singleGenreAction

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
            when (val singleGenre = environment.catalogueRepository.getSingleGenre(genreId, page)) {
                is Result.Success -> {
                    val data = singleGenre.data.data
                    fetchSingleGenreList.addAll(data.toTitleListModel())
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
            when (val singleGenre = environment.catalogueRepository.getSingleGenre(genreId, page)) {
                is Result.Success -> {
                    val data = singleGenre.data.data
                    when (genreId) {
                        265 -> _singleGenreAnimation.value = data.toTitleListModel()
                        258 -> _singleGenreComedy.value = data.toTitleListModel()
                        260 -> _singleGenreMelodrama.value = data.toTitleListModel()
                        255 -> _singleGenreHorror.value = data.toTitleListModel()
                        266 -> _singleGenreAdventure.value = data.toTitleListModel()
                        248 -> _singleGenreAction.value = data.toTitleListModel()
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
            when (val singleStudio = environment.catalogueRepository.getSingleStudio(studioId, page)) {
                is Result.Success -> {
                    val data = singleStudio.data.data
                    fetchSingleStudioList.addAll(data.toTitleListModel())
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