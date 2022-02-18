package com.lukakordzaia.streamflowtv.ui.genres

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.usecases.SingleGenreUseCase
import com.lukakordzaia.core.network.LoadingState
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.utils.AppConstants
import kotlinx.coroutines.*

class SingleGenreViewModel(
    private val singleGenreUseCase: SingleGenreUseCase
) : BaseViewModel() {
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

    private suspend fun getSingleGenreForTv(genreId: Int, page: Int = 1) {
        when (val result = singleGenreUseCase.invoke(Pair(genreId, page))) {
            is ResultDomain.Success -> {
                val data = result.data
                when (genreId) {
                    265 -> _singleGenreAnimation.postValue(data)
                    258 -> _singleGenreComedy.postValue(data)
                    260 -> _singleGenreMelodrama.postValue(data)
                    255 -> _singleGenreHorror.postValue(data)
                    266 -> _singleGenreAdventure.postValue(data)
                    248 -> _singleGenreAction.postValue(data)
                }
            }
            is ResultDomain.Error -> {
                when (result.exception) {
                    AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                    else -> newToastMessage("ჟანრი - ${result.exception}")
                }
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