package com.lukakordzaia.streamflow.network.models.trakttv.response


import com.google.gson.annotations.SerializedName

data class AddTitleToTraktListResponse(
    @SerializedName("added")
    val added: Added,
    @SerializedName("existing")
    val existing: Existing,
    @SerializedName("not_found")
    val notFound: NotFound
) {
    data class Added(
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

    data class Existing(
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