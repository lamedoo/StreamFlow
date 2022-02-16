package com.lukakordzaia.streamflowtv.ui.tvsingletitle.tvtitlerelated

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.datamodels.SingleTitleModel
import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.models.imovies.response.singletitle.GetSingleTitleCastResponse
import com.lukakordzaia.core.network.toTitleListModel
import kotlinx.coroutines.launch

class TvRelatedViewModel : BaseViewModel() {
    private val _castData = MutableLiveData<List<GetSingleTitleCastResponse.Data>>()
    val castResponseDataGetSingle: LiveData<List<GetSingleTitleCastResponse.Data>> = _castData

    private val _singleTitleRelated = MutableLiveData<List<SingleTitleModel>>()
    val singleTitleRelated: LiveData<List<SingleTitleModel>> = _singleTitleRelated

    fun getSingleTitleCast(titleId: Int) {
        viewModelScope.launch {
            when (val cast = environment.singleTitleRepository.getSingleTitleCast(titleId, "cast")) {
                is ResultData.Success -> {
                    val data = cast.data.data

                    _castData.value = data
                }
                is ResultData.Error -> {
                    newToastMessage("მსახიობები - ${cast.exception}")
                }
            }
        }
    }

    fun getSingleTitleRelated(titleId: Int) {
        viewModelScope.launch {
            when (val related = environment.singleTitleRepository.getSingleTitleRelated(titleId)) {
                is ResultData.Success -> {
                    val data = related.data.data
                    _singleTitleRelated.value = data.toTitleListModel()
                }
                is ResultData.Error -> {
                    newToastMessage("მსგავსი - ${related.exception}")
                }
            }
        }
    }
}