package com.lukakordzaia.streamflow.datamodels


import com.google.gson.annotations.SerializedName

data class SingleTitleData(
    @SerializedName("data")
    val `data`: Data
) {
    data class Data(
        @SerializedName("adjaraId")
        val adjaraId: Int?,
        @SerializedName("adult")
        val adult: Boolean?,
        @SerializedName("akaNames")
        val akaNames: AkaNames?,
        @SerializedName("budget")
        val budget: String?,
        @SerializedName("canBePlayed")
        val canBePlayed: Boolean?,
        @SerializedName("countries")
        val countries: Countries,
        @SerializedName("cover")
        val cover: Cover?,
        @SerializedName("covers")
        val covers: Covers?,
        @SerializedName("duration")
        val duration: Int?,
        @SerializedName("genres")
        val genres: Genres,
        @SerializedName("hasSubtitles")
        val hasSubtitles: Boolean?,
        @SerializedName("id")
        val id: Int,
        @SerializedName("imdbUrl")
        val imdbUrl: String?,
        @SerializedName("income")
        val income: String?,
        @SerializedName("isTvShow")
        val isTvShow: Boolean,
        @SerializedName("languages")
        val languages: Languages?,
        @SerializedName("originalName")
        val originalName: String?,
        @SerializedName("oscarNominations")
        val oscarNominations: OscarNominations?,
        @SerializedName("parentalControlRate")
        val parentalControlRate: String?,
        @SerializedName("plot")
        val plot: Plot,
        @SerializedName("plots")
        val plots: Plots?,
        @SerializedName("poster")
        val poster: String?,
        @SerializedName("posters")
        val posters: Posters,
        @SerializedName("primaryName")
        val primaryName: String,
        @SerializedName("rating")
        val rating: Rating,
        @SerializedName("regionAllowed")
        val regionAllowed: Boolean?,
        @SerializedName("releaseDate")
        val releaseDate: String?,
        @SerializedName("seasons")
        val seasons: Seasons?,
        @SerializedName("secondaryName")
        val secondaryName: String?,
        @SerializedName("studios")
        val studios: Studios?,
        @SerializedName("tertiaryName")
        val tertiaryName: String?,
        @SerializedName("trailers")
        val trailers: Trailers,
        @SerializedName("tvcom")
        val tvcom: Tvcom?,
        @SerializedName("userFollows")
        val userFollows: UserFollows?,
        @SerializedName("userMovieLists")
        val userMovieLists: UserMovieLists?,
        @SerializedName("userRating")
        val userRating: UserRating?,
        @SerializedName("userSubscribed")
        val userSubscribed: UserSubscribed?,
        @SerializedName("userWantsToWatch")
        val userWantsToWatch: UserWantsToWatch?,
        @SerializedName("userWatch")
        val userWatch: UserWatch?,
        @SerializedName("vast")
        val vast: Vast?,
        @SerializedName("watchCount")
        val watchCount: Int?,
        @SerializedName("year")
        val year: Int?
    ) {
        data class AkaNames(
            @SerializedName("data")
            val `data`: List<DataAkaNames>?
        ) {
            data class DataAkaNames(
                @SerializedName("name")
                val name: String?,
                @SerializedName("type")
                val type: String?
            )
        }

        data class Countries(
            @SerializedName("data")
            val `data`: List<DataCountries>
        ) {
            data class DataCountries(
                @SerializedName("id")
                val id: Int?,
                @SerializedName("primaryName")
                val primaryName: String?,
                @SerializedName("secondaryName")
                val secondaryName: String?,
                @SerializedName("tertiaryName")
                val tertiaryName: String?
            )
        }

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

        data class Genres(
            @SerializedName("data")
            val `data`: List<DataGenres>
        ) {
            data class DataGenres(
                @SerializedName("backgroundImage")
                val backgroundImage: String?,
                @SerializedName("id")
                val id: Int?,
                @SerializedName("primaryName")
                val primaryName: String?,
                @SerializedName("secondaryName")
                val secondaryName: String?,
                @SerializedName("tertiaryName")
                val tertiaryName: String?
            )
        }

        data class Languages(
            @SerializedName("data")
            val `data`: List<DataLanguages>?
        ) {
            data class DataLanguages(
                @SerializedName("code")
                val code: String?,
                @SerializedName("primaryName")
                val primaryName: String?,
                @SerializedName("primaryNameTurned")
                val primaryNameTurned: String?,
                @SerializedName("secondaryName")
                val secondaryName: String?,
                @SerializedName("tertiaryName")
                val tertiaryName: String?
            )
        }

        data class OscarNominations(
            @SerializedName("data")
            val `data`: List<Any?>?
        )

        data class Plot(
            @SerializedName("data")
            val `data`: Data
        ) {
            data class Data(
                @SerializedName("description")
                val description: String,
                @SerializedName("language")
                val language: String
            )
        }

        data class Plots(
            @SerializedName("data")
            val `data`: List<DataPlots>?
        ) {
            data class DataPlots(
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
            @SerializedName("imovies")
            val imovies: Imovies?,
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

            data class Imovies(
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

        data class Seasons(
            @SerializedName("data")
            val `data`: List<DataSeasons>
        ) {
            data class DataSeasons(
                @SerializedName("episodesCount")
                val episodesCount: Int?,
                @SerializedName("movieId")
                val movieId: Int?,
                @SerializedName("name")
                val name: String?,
                @SerializedName("number")
                val number: Int
            )
        }

        data class Studios(
            @SerializedName("data")
            val `data`: List<DataStudios>?
        ) {
            data class DataStudios(
                @SerializedName("backgroundImage")
                val backgroundImage: String?,
                @SerializedName("backgroundImageNarrow")
                val backgroundImageNarrow: String?,
                @SerializedName("id")
                val id: Int?,
                @SerializedName("isFeatured")
                val isFeatured: Boolean?,
                @SerializedName("lightPoster")
                val lightPoster: String?,
                @SerializedName("name")
                val name: String?,
                @SerializedName("poster")
                val poster: String?,
                @SerializedName("rating")
                val rating: Double?
            )
        }

        data class Trailers(
            @SerializedName("data")
            val `data`: List<DataTrailers>
        ) {
            data class DataTrailers(
                @SerializedName("fileUrl")
                val fileUrl: String?,
                @SerializedName("id")
                val id: Int?,
                @SerializedName("language")
                val language: String?,
                @SerializedName("name")
                val name: String?
            )
        }

        data class Tvcom(
            @SerializedName("data")
            val `data`: Data?
        ) {
            data class Data(
                @SerializedName("url")
                val url: String?
            )
        }

        data class UserFollows(
            @SerializedName("data")
            val `data`: Data?
        ) {
            data class Data(
                @SerializedName("status")
                val status: Boolean?
            )
        }

        data class UserMovieLists(
            @SerializedName("data")
            val `data`: List<Any?>?
        )

        data class UserRating(
            @SerializedName("data")
            val `data`: Data?
        ) {
            data class Data(
                @SerializedName("rate")
                val rate: Int?,
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

        data class Vast(
            @SerializedName("data")
            val `data`: Data?
        ) {
            data class Data(
                @SerializedName("pause_url")
                val pauseUrl: Any?,
                @SerializedName("url")
                val url: String?
            )
        }
    }
}