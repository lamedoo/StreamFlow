package com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitlerelated

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.usecases.SingleTitleCastUseCase
import com.lukakordzaia.core.domain.usecases.SingleTitleRelatedUseCase
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleCastResponse
import com.lukakordzaia.core.utils.AppConstants
import kotlinx.coroutines.launch

class TvRelatedViewModel(
    private val singleTitleCastUseCase: SingleTitleCastUseCase,
    private val singleTitleRelatedUseCase: SingleTitleRelatedUseCase,
) : BaseViewModel() {
    private val _castData = MutableLiveData<List<GetSingleTitleCastResponse.Data>>()
    val castResponseDataGetSingle: LiveData<List<GetSingleTitleCastResponse.Data>> = _castData

    private val _singleTitleRelated = MutableLiveData<List<SingleTitleModel>>()
    val singleTitleRelated: LiveData<List<SingleTitleModel>> = _singleTitleRelated

    fun getSingleTitleCast(titleId: Int) {
        viewModelScope.launch {
            when (val result = singleTitleCastUseCase.invoke(Pair(titleId, "cast"))) {
                is ResultDomain.Success -> {
                    val data = result.data.data
                    _castData.postValue(data)
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                        else -> newToastMessage("მსახიობები - ${result.exception}")
                    }
                }
            }
        }
    }

    fun getSingleTitleRelated(titleId: Int) {
        viewModelScope.launch {
            when (val result = singleTitleRelatedUseCase.invoke(titleId)) {
                is ResultDomain.Success -> {
                    _singleTitleRelated.postValue(result.data)
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> {}
                        else -> newToastMessage("მსგავსი - ${result.exception}")
                    }
                }
            }
        }
    }
}