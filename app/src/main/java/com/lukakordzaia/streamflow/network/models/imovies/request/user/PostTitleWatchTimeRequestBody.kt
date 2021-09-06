package com.lukakordzaia.streamflow.network.models.imovies.request.user


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostTitleWatchTimeRequestBody(
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("language")
    val language: String,
    @SerializedName("progress")
    val progress: Int,
    @SerializedName("quality")
    val quality: String
) : Parcelable