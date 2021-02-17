package com.lukakordzaia.streamflow.repository

import com.lukakordzaia.streamflow.datamodels.SendTraktvClientId
import com.lukakordzaia.streamflow.datamodels.TraktvDeviceCode
import com.lukakordzaia.streamflow.network.Result
import com.lukakordzaia.streamflow.network.RetrofitBuilder
import com.lukakordzaia.streamflow.network.traktv.TraktvCall
import com.lukakordzaia.streamflow.utils.AppConstants

class TraktvRepository(retrofitBuilder: RetrofitBuilder): TraktvCall() {
    private val service = retrofitBuilder.buildTraktvService()

    suspend fun getDeviceCode(): Result<TraktvDeviceCode> {
        return traktvCall { service.getDeviceCode(SendTraktvClientId(AppConstants.TRAKTV_CLIENT_ID)) }
    }
}