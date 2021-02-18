package com.lukakordzaia.streamflow.datamodels


import com.google.gson.annotations.SerializedName

data class AddMovieToTraktList(
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