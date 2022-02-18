package com.lukakordzaia.streamflowtv.ui.genres

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.datamodels.SingleTitleModel
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.Result
import com.lukakordzaia.core.network.toTitleListModel
import kotlinx.coroutines.*

class SingleGenreViewModel : BaseViewModel() {
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

    private suspend fun getSingleGenreForTv(genreId: Int, page: Int = 1) {
            when (val singleGenre = environment.catalogueRepository.getSingleGenre(genreId, page)) {
                is Result.Success -> {
                    val data = singleGenre.data.data
                    when (genreId) {
                        265 -> _singleGenreAnimation.postValue(data.toTitleListModel())
                        258 -> _singleGenreComedy.postValue(data.toTitleListModel())
                        260 -> _singleGenreMelodrama.postValue(data.toTitleListModel())
                        255 -> _singleGenreHorror.postValue(data.toTitleListModel())
                        266 -> _singleGenreAdventure.postValue(data.toTitleListModel())
                        248 -> _singleGenreAction.postValue(data.toTitleListModel())
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

    fun fetchContentTv() {
        setGeneralLoader(LoadingState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            coroutineScope {
                val animationDeferred = async { getSingleGenreForTv(265) }
                val comedyDeferred = async { getSingleGenreForTv(258) }
                val melodramaDeferred = async { getSingleGenreForTv(260) }
                val horrorDeferred = async { getSingleGenreForTv(255) }
                val adventureDeferred = async { getSingleGenreForTv(266) }
                val actionsDeferred = async { getSingleGenreForTv(248) }

                animationDeferred.await()
                comedyDeferred.await()
                melodramaDeferred.await()
                horrorDeferred.await()
                adventureDeferred.await()
                actionsDeferred.await()
            }
            setGeneralLoader(LoadingState.LOADED)
        }
    }
}