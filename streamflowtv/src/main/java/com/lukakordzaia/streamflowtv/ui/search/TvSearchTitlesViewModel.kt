package com.lukakordzaia.streamflowtv.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lukakordzaia.core.baseclasses.BaseViewModel
import com.lukakordzaia.core.domain.domainmodels.SingleTitleModel
import com.lukakordzaia.core.domain.usecases.SearchTitleUseCase
import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.network.toTitleListModel
import com.lukakordzaia.core.utils.AppConstants
import kotlinx.coroutines.launch

class TvSearchTitlesViewModel(
    private val searchTitleUseCase: SearchTitleUseCase
) : BaseViewModel() {

    private val _searchList = MutableLiveData<List<SingleTitleModel>>()
    val searchList: LiveData<List<SingleTitleModel>> = _searchList

    fun getSearchTitles(keywords: String, page: Int) {
        viewModelScope.launch {
            when (val result = searchTitleUseCase.invoke(Pair(keywords, page))) {
                is ResultDomain.Success -> {
                    _searchList.postValue(result.data)
                }
                is ResultDomain.Error -> {
                    when (result.exception) {
                        AppConstants.NO_INTERNET_ERROR -> setNoInternet()
                        else -> newToastMessage("ძებნა - ${result.exception}")
                    }
                }
            }
        }
    }
}