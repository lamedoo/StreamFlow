package com.lukakordzaia.streamflow.network.models.trakttv.request

import com.google.gson.annotations.SerializedName

data class GetDeviceCodeRequestBody(
    @SerializedName("client_id")
    val clientId: String
)