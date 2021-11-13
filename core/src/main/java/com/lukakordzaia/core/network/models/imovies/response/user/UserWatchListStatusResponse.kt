package com.lukakordzaia.core.network.models.imovies.response.user


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserWatchListStatusResponse(
    @SerializedName("message")
    val message: String
) : Parcelable