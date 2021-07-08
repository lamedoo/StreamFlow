package com.lukakordzaia.streamflow.network.models.trakttv.request


import com.google.gson.annotations.SerializedName

data class AddMovieToTraktListRequestBody(
    @SerializedName("movies")
    val movies: List<Movy>
) {
    data class Movy(
        @SerializedName("ids")
        val ids: Ids
    ) {
        data class Ids(
            @SerializedName("imdb")
            val imdb: String
        )
    }
}