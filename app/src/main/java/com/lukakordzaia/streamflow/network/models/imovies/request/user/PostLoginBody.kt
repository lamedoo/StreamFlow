package com.lukakordzaia.streamflow.network.models.imovies.request.user


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostLoginBody(
    @SerializedName("password")
    val password: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("client_id")
    val clientId: Int = 3,
    @SerializedName("grant_type")
    val grantType: String = "password",
) : Parcelable