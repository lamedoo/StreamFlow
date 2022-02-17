package com.lukakordzaia.core.network.models.imovies.response.user


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserWatchListStatusResponse(
    @SerializedName("message")
    val message: String
) : Parcelable