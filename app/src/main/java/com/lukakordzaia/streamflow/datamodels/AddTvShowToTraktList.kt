package com.lukakordzaia.streamflow.datamodels


import com.google.gson.annotations.SerializedName

data class AddTvShowToTraktList(
    @SerializedName("shows")
    val tvShows: List<Showy>
) {
    data class Showy(
        @SerializedName("ids")
        val ids: Ids
    ) {
        data class Ids(
            @SerializedName("imdb")
            val imdb: String
        )
    }
}