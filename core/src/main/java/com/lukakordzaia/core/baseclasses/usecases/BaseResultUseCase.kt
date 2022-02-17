package com.lukakordzaia.core.baseclasses.usecases

import com.lukakordzaia.core.network.ResultData
import com.lukakordzaia.core.network.ResultDomain
import com.lukakordzaia.core.utils.AppConstants
import java.lang.Exception

abstract class BaseResultUseCase<ARG_TYPE, DATA_TYPE : Any, DOMAIN_TYPE : Any, RETURN_TYPE : Any> : BaseUseCase<ARG_TYPE, RETURN_TYPE> {

    fun transformToDomain(apiCall: ResultData<DATA_TYPE>, transform: (data: DATA_TYPE) -> DOMAIN_TYPE): ResultDomain<DOMAIN_TYPE, String> {
        return when (val result = getData(apiCall)) {
            is ResultData.Success -> {
                try {
                    ResultDomain.Success(transform(result.data))
                } catch (ex: Exception) {
                    ResultDomain.Error(ex.toString())
                }
            }
            is ResultData.Error -> {
                ResultDomain.Error(result.exception)
            }
            is ResultData.Internet -> {
                ResultDomain.Error(AppConstants.NO_INTERNET_ERROR)
            }
        }
    }

    private fun getData(apiCall: ResultData<DATA_TYPE>): ResultData<DATA_TYPE> {
        return when (apiCall) {
            is ResultData.Success -> {
                ResultData.Success(apiCall.data)
            }
            is ResultData.Error -> {
                ResultData.Error(apiCall.exception)
            }
            is ResultData.Internet -> {
                ResultData.Internet
            }
        }
    }
}