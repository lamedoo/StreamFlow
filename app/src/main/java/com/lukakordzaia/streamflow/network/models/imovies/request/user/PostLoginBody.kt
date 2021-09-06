package com.lukakordzaia.streamflow.network.models.imovies.request.user


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostLoginBody(
    @SerializedName("client_id")
    val clientId: Int,
    @SerializedName("grant_type")
    val grantType: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("username")
    val username: String
) : Parcelable