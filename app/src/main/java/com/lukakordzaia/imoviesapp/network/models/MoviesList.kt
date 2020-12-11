package com.lukakordzaia.imoviesapp.network.models


import com.google.gson.annotations.SerializedName

data class MoviesList(
    @SerializedName("data")
    val `data`: List<Data>?,
    @SerializedName("meta")
    val meta: Meta?
) {
    data class Data(
        @SerializedName("adjaraId")
        val adjaraId: Int?,
        @SerializedName("adult")
        val adult: Boolean?,
        @SerializedName("budget")
        val budget: String?,
        @SerializedName("canBePlayed")
        val canBePlayed: Boolean?,
        @SerializedName("cover")
        val cover: Cover?,
        @SerializedName("covers")
        val covers: Covers?,
        @SerializedName("duration")
        val duration: Int?,
        @SerializedName("hasSubtitles")
        val hasSubtitles: Boolean?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("imdbUrl")
        val imdbUrl: String?,
        @SerializedName("income")
        val income: String?,
        @SerializedName("isTvShow")
        val isTvShow: Boolean?,
        @SerializedName("languages")
        val languages: Languages?,
        @SerializedName("originalName")
        val originalName: String?,
        @SerializedName("parentalControlRate")
        val parentalControlRate: String?,
        @SerializedName("plot")
        val plot: Plot?,
        @SerializedName("poster")
        val poster: String?,
        @SerializedName("posters")
        val posters: Posters?,
        @SerializedName("primaryName")
        val primaryName: String?,
        @SerializedName("rating")
        val rating: Rating?,
        @SerializedName("regionAllowed")
        val regionAllowed: Boolean?,
        @SerializedName("releaseDate")
        val releaseDate: String?,
        @SerializedName("secondaryName")
        val secondaryName: String?,
        @SerializedName("tertiaryName")
        val tertiaryName: String?,
        @SerializedName("trailers")
        val trailers: Trailers?,
        @SerializedName("userFollows")
        val userFollows: UserFollows?,
        @SerializedName("userSubscribed")
        val userSubscribed: UserSubscribed?,
        @SerializedName("userWantsToWatch")
        val userWantsToWatch: UserWantsToWatch?,
        @SerializedName("userWatch")
        val userWatch: UserWatch?,
        @SerializedName("watchCount")
        val watchCount: Int?,
        @SerializedName("year")
        val year: Int?
    ) {
        data class Cover(
            @SerializedName("large")
            val large: String?,
            @SerializedName("small")
            val small: String?
        )

        data class Covers(
            @SerializedName("data")
            val `data`: Data?
        ) {
            data class Data(
                @SerializedName("blurhash")
                val blurhash: String?,
                @SerializedName("imageHeight")
                val imageHeight: String?,
                @SerializedName("position")
                val position: String?,
                @SerializedName("positionPercentage")
                val positionPercentage: String?,
                @SerializedName("1050")
                val x1050: String?,
                @SerializedName("145")
                val x145: String?,
                @SerializedName("1920")
                val x1920: String?,
                @SerializedName("367")
                val x367: String?,
                @SerializedName("510")
                val x510: String?
            )
        }

        data class Languages(
            @SerializedName("data")
            val `data`: List<Any?>?
        )

        data class Plot(
            @SerializedName("data")
            val `data`: Data?
        ) {
            data class Data(
                @SerializedName("description")
                val description: String?,
                @SerializedName("language")
                val language: String?
            )
        }

        data class Posters(
            @SerializedName("data")
            val `data`: Data?
        ) {
            data class Data(
                @SerializedName("blurhash")
                val blurhash: String?,
                @SerializedName("240")
                val x240: String?
            )
        }

        data class Rating(
            @SerializedName("imdb")
            val imdb: Imdb?,
            @SerializedName("metacritic")
            val metacritic: Metacritic?,
            @SerializedName("rotten")
            val rotten: Rotten?
        ) {
            data class Imdb(
                @SerializedName("score")
                val score: Double?,
                @SerializedName("voters")
                val voters: Int?
            )

            data class Metacritic(
                @SerializedName("score")
                val score: Int?,
                @SerializedName("voters")
                val voters: Any?
            )

            data class Rotten(
                @SerializedName("score")
                val score: Int?,
                @SerializedName("voters")
                val voters: Any?
            )
        }

        data class Trailers(
            @SerializedName("data")
            val `data`: List<Any?>?
        )

        data class UserFollows(
            @SerializedName("data")
            val `data`: Data?
        ) {
            data class Data(
                @SerializedName("status")
                val status: Boolean?
            )
        }

        data class UserSubscribed(
            @SerializedName("data")
            val `data`: Data?
        ) {
            data class Data(
                @SerializedName("status")
                val status: Boolean?
            )
        }

        data class UserWantsToWatch(
            @SerializedName("data")
            val `data`: Data?
        ) {
            data class Data(
                @SerializedName("status")
                val status: Boolean?
            )
        }

        data class UserWatch(
            @SerializedName("data")
            val `data`: Data?
        ) {
            data class Data(
                @SerializedName("duration")
                val duration: Int?,
                @SerializedName("episode")
                val episode: Any?,
                @SerializedName("language")
                val language: String?,
                @SerializedName("progress")
                val progress: Int?,
                @SerializedName("quality")
                val quality: String?,
                @SerializedName("season")
                val season: Any?,
                @SerializedName("updateDate")
                val updateDate: String?,
                @SerializedName("visible")
                val visible: Boolean?,
                @SerializedName("watched")
                val watched: Boolean?
            )
        }
    }

    data class Meta(
        @SerializedName("pagination")
        val pagination: Pagination?
    ) {
        data class Pagination(
            @SerializedName("count")
            val count: Int?,
            @SerializedName("current_page")
            val currentPage: Int?,
            @SerializedName("links")
            val links: Links?,
            @SerializedName("per_page")
            val perPage: Int?,
            @SerializedName("total")
            val total: Int?,
            @SerializedName("total_pages")
            val totalPages: Int?
        ) {
            data class Links(
                @SerializedName("next")
                val next: String?
            )
        }
    }
}