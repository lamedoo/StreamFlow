package com.lukakordzaia.streamflow.network.models.imovies.response.user


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostWatchTimeResponse(
    @SerializedName("data")
    val `data`: Data
) : Parcelable {
    @Parcelize
    data class Data(
        @SerializedName("duration")
        val duration: Int,
        @SerializedName("episode")
        val episode: Int,
        @SerializedName("language")
        val language: String,
        @SerializedName("progress")
        val progress: Int,
        @SerializedName("quality")
        val quality: String,
        @SerializedName("season")
        val season: Int,
        @SerializedName("updateDate")
        val updateDate: String,
        @SerializedName("visible")
        val visible: Boolean,
        @SerializedName("watched")
        val watched: Boolean
    ) : Parcelable
}