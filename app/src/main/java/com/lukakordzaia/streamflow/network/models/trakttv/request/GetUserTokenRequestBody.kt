package com.lukakordzaia.streamflow.network.models.trakttv.request


import com.google.gson.annotations.SerializedName

data class GetUserTokenRequestBody(
    @SerializedName("client_id")
    val clientId: String,
    @SerializedName("client_secret")
    val clientSecret: String,
    @SerializedName("code")
    val code: String
)