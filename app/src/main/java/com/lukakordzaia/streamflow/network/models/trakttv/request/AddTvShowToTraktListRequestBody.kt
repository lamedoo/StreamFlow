package com.lukakordzaia.streamflow.network.models.trakttv.request


import com.google.gson.annotations.SerializedName

data class AddTvShowToTraktListRequestBody(
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