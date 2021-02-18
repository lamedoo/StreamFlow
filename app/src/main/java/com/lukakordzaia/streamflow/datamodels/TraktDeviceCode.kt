package com.lukakordzaia.streamflow.datamodels


import com.google.gson.annotations.SerializedName

data class SendTraktvClientId(
        @SerializedName("client_id")
        val clientId: String
)

data class TraktvDeviceCode(
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