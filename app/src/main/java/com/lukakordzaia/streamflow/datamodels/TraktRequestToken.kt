package com.lukakordzaia.streamflow.datamodels


import com.google.gson.annotations.SerializedName

data class TraktRequestToken(
    @SerializedName("client_id")
    val clientId: String,
    @SerializedName("client_secret")
    val clientSecret: String,
    @SerializedName("code")
    val code: String
)