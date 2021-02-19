package com.lukakordzaia.streamflow.datamodels


import com.google.gson.annotations.SerializedName

data class RemoveFromTraktListResponse(
    @SerializedName("deleted")
    val deleted: Deleted,
    @SerializedName("not_found")
    val notFound: NotFound
) {
    data class Deleted(
        @SerializedName("episodes")
        val episodes: Int,
        @SerializedName("movies")
        val movies: Int,
        @SerializedName("people")
        val people: Int,
        @SerializedName("seasons")
        val seasons: Int,
        @SerializedName("shows")
        val shows: Int
    )

    data class NotFound(
        @SerializedName("episodes")
        val episodes: List<Any>,
        @SerializedName("movies")
        val movies: List<Movy>,
        @SerializedName("people")
        val people: List<Any>,
        @SerializedName("seasons")
        val seasons: List<Any>,
        @SerializedName("shows")
        val shows: List<Any>
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
}