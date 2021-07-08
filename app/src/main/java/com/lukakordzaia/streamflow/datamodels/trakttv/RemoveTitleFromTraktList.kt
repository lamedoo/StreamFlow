package com.lukakordzaia.streamflow.datamodels.trakttv


import com.google.gson.annotations.SerializedName

data class RemoveTitleFromTraktList(
    @SerializedName("episodes")
    val episodes: List<Episode>,
    @SerializedName("movies")
    val movies: List<Movy>,
    @SerializedName("people")
    val people: List<People>,
    @SerializedName("seasons")
    val seasons: List<Season>,
    @SerializedName("shows")
    val shows: List<Show>
) {
    data class Episode(
        @SerializedName("ids")
        val ids: Ids
    ) {
        data class Ids(
            @SerializedName("imdb")
            val imdb: String,
            @SerializedName("tmdb")
            val tmdb: Int,
            @SerializedName("trakt")
            val trakt: Int,
            @SerializedName("tvdb")
            val tvdb: Int
        )
    }

    data class Movy(
        @SerializedName("ids")
        val ids: Ids
    ) {
        data class Ids(
            @SerializedName("imdb")
            val imdb: String,
            @SerializedName("trakt")
            val trakt: Int
        )
    }

    data class People(
        @SerializedName("ids")
        val ids: Ids,
        @SerializedName("name")
        val name: String
    ) {
        data class Ids(
            @SerializedName("imdb")
            val imdb: String,
            @SerializedName("slug")
            val slug: String,
            @SerializedName("tmdb")
            val tmdb: Int,
            @SerializedName("trakt")
            val trakt: Int
        )
    }

    data class Season(
        @SerializedName("ids")
        val ids: Ids
    ) {
        data class Ids(
            @SerializedName("tmdb")
            val tmdb: Int,
            @SerializedName("trakt")
            val trakt: Int,
            @SerializedName("tvdb")
            val tvdb: Int
        )
    }

    data class Show(
        @SerializedName("ids")
        val ids: Ids,
        @SerializedName("seasons")
        val seasons: List<Season>
    ) {
        data class Ids(
            @SerializedName("trakt")
            val trakt: Int
        )

        data class Season(
            @SerializedName("episodes")
            val episodes: List<Episode>,
            @SerializedName("number")
            val number: Int
        ) {
            data class Episode(
                @SerializedName("number")
                val number: Int
            )
        }
    }
}