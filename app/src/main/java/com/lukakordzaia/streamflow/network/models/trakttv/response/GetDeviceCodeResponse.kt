package com.lukakordzaia.streamflow.network.models.trakttv.response


import com.google.gson.annotations.SerializedName

data class GetDeviceCodeResponse(
    @SerializedName("device_code")
    val deviceCode: String,
    @SerializedName("expires_in")
    val expiresIn: Int,
    @SerializedName("interval")
    val interval: Int,
    @SerializedName("user_code")
    val userCode: String,
    @SerializedName("verification_url")
    val verificationUrl: String
)