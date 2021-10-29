package com.lukakordzaia.streamflow.network.models.imovies.response.newseries


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetNewSeriesResponse(
    @SerializedName("data")
    val `data`: List<Data>
) : Parcelable {
    @Parcelize
    data class Data(
        @SerializedName("movies")
        val movies: Movies,
        @SerializedName("period")
        val period: String
    ) : Parcelable {
        @Parcelize
        data class Movies(
            @SerializedName("data")
            val `data`: List<Data>
        ) : Parcelable {
            @Parcelize
            data class Data(
                @SerializedName("adjaraId")
                val adjaraId: Int,
                @SerializedName("adult")
                val adult: Boolean,
                @SerializedName("budget")
                val budget: String,
                @SerializedName("canBePlayed")
                val canBePlayed: Boolean,
                @SerializedName("cover")
                val cover: Cover,
                @SerializedName("covers")
                val covers: Covers,
                @SerializedName("duration")
                val duration: Int,
                @SerializedName("genres")
                val genres: Genres,
                @SerializedName("hasSubtitles")
                val hasSubtitles: Boolean,
                @SerializedName("id")
                val id: Int,
                @SerializedName("imdbUrl")
                val imdbUrl: String,
                @SerializedName("income")
                val income: String,
                @SerializedName("isTvShow")
                val isTvShow: Boolean,
                @SerializedName("languages")
                val languages: Languages,
                @SerializedName("lastUpdatedSeries")
                val lastUpdatedSeries: LastUpdatedSeries,
                @SerializedName("originalName")
                val originalName: String,
                @SerializedName("parentalControlRate")
                val parentalControlRate: Any,
                @SerializedName("plot")
                val plot: Plot,
                @SerializedName("plots")
                val plots: Plots,
                @SerializedName("poster")
                val poster: String,
                @SerializedName("posters")
                val posters: Posters,
                @SerializedName("primaryName")
                val primaryName: String,
                @SerializedName("rating")
                val rating: Rating,
                @SerializedName("regionAllowed")
                val regionAllowed: Boolean,
                @SerializedName("releaseDate")
                val releaseDate: String,
                @SerializedName("secondaryName")
                val secondaryName: String,
                @SerializedName("tertiaryName")
                val tertiaryName: String,
                @SerializedName("trailers")
                val trailers: Trailers,
                @SerializedName("userFollows")
                val userFollows: UserFollows,
                @SerializedName("userSubscribed")
                val userSubscribed: UserSubscribed,
                @SerializedName("userWantsToWatch")
                val userWantsToWatch: UserWantsToWatch,
                @SerializedName("userWatch")
                val userWatch: UserWatch,
                @SerializedName("watchCount")
                val watchCount: Int,
                @SerializedName("year")
                val year: Int
            ) : Parcelable {
                @Parcelize
                data class Cover(
                    @SerializedName("large")
                    val large: String,
                    @SerializedName("small")
                    val small: String
                ) : Parcelable

                @Parcelize
                data class Covers(
                    @SerializedName("data")
                    val `data`: Data
                ) : Parcelable {
                    @Parcelize
                    data class Data(
                        @SerializedName("blurhash")
                        val blurhash: String,
                        @SerializedName("imageHeight")
                        val imageHeight: Any,
                        @SerializedName("position")
                        val position: String,
                        @SerializedName("positionPercentage")
                        val positionPercentage: String,
                        @SerializedName("1050")
                        val x1050: String,
                        @SerializedName("145")
                        val x145: String,
                        @SerializedName("1920")
                        val x1920: String,
                        @SerializedName("367")
                        val x367: String,
                        @SerializedName("510")
                        val x510: String
                    ) : Parcelable
                }

                @Parcelize
                data class Genres(
                    @SerializedName("data")
                    val `data`: List<Data>
                ) : Parcelable {
                    @Parcelize
                    data class Data(
                        @SerializedName("backgroundImage")
                        val backgroundImage: String,
                        @SerializedName("id")
                        val id: Int,
                        @SerializedName("primaryName")
                        val primaryName: String,
                        @SerializedName("secondaryName")
                        val secondaryName: String,
                        @SerializedName("tertiaryName")
                        val tertiaryName: String
                    ) : Parcelable
                }

                @Parcelize
                data class Languages(
                    @SerializedName("data")
                    val `data`: List<Data>
                ) : Parcelable {
                    @Parcelize
                    data class Data(
                        @SerializedName("code")
                        val code: String,
                        @SerializedName("primaryName")
                        val primaryName: String,
                        @SerializedName("primaryNameTurned")
                        val primaryNameTurned: String,
                        @SerializedName("secondaryName")
                        val secondaryName: String,
                        @SerializedName("tertiaryName")
                        val tertiaryName: String
                    ) : Parcelable
                }

                @Parcelize
                data class LastUpdatedSeries(
                    @SerializedName("data")
                    val `data`: Data
                ) : Parcelable {
                    @Parcelize
                    data class Data(
                        @SerializedName("episode")
                        val episode: Int,
                        @SerializedName("season")
                        val season: Int
                    ) : Parcelable
                }

                @Parcelize
                data class Plot(
                    @SerializedName("data")
                    val `data`: Data
                ) : Parcelable {
                    @Parcelize
                    data class Data(
                        @SerializedName("description")
                        val description: String,
                        @SerializedName("language")
                        val language: String
                    ) : Parcelable
                }

                @Parcelize
                data class Plots(
                    @SerializedName("data")
                    val `data`: List<Data>
                ) : Parcelable {
                    @Parcelize
                    data class Data(
                        @SerializedName("description")
                        val description: String,
                        @SerializedName("language")
                        val language: String
                    ) : Parcelable
                }

                @Parcelize
                data class Posters(
                    @SerializedName("data")
                    val `data`: Data
                ) : Parcelable {
                    @Parcelize
                    data class Data(
                        @SerializedName("blurhash")
                        val blurhash: String,
                        @SerializedName("240")
                        val x240: String
                    ) : Parcelable
                }

                @Parcelize
                data class Rating(
                    @SerializedName("imdb")
                    val imdb: Imdb,
                    @SerializedName("imovies")
                    val imovies: Imovies,
                    @SerializedName("metacritic")
                    val metacritic: Metacritic,
                    @SerializedName("rotten")
                    val rotten: Rotten
                ) : Parcelable {
                    @Parcelize
                    data class Imdb(
                        @SerializedName("score")
                        val score: Int,
                        @SerializedName("voters")
                        val voters: Int
                    ) : Parcelable

                    @Parcelize
                    data class Imovies(
                        @SerializedName("score")
                        val score: Int,
                        @SerializedName("voters")
                        val voters: Int
                    ) : Parcelable

                    @Parcelize
                    data class Metacritic(
                        @SerializedName("score")
                        val score: Int,
                        @SerializedName("voters")
                        val voters: Any
                    ) : Parcelable

                    @Parcelize
                    data class Rotten(
                        @SerializedName("score")
                        val score: Int,
                        @SerializedName("voters")
                        val voters: Any
                    ) : Parcelable
                }

                @Parcelize
                data class Trailers(
                    @SerializedName("data")
                    val `data`: List<Data>
                ) : Parcelable {
                    @Parcelize
                    data class Data(
                        @SerializedName("fileUrl")
                        val fileUrl: String,
                        @SerializedName("id")
                        val id: Int,
                        @SerializedName("language")
                        val language: String,
                        @SerializedName("name")
                        val name: String
                    ) : Parcelable
                }

                @Parcelize
                data class UserFollows(
                    @SerializedName("data")
                    val `data`: Data
                ) : Parcelable {
                    @Parcelize
                    data class Data(
                        @SerializedName("status")
                        val status: Boolean
                    ) : Parcelable
                }

                @Parcelize
                data class UserSubscribed(
                    @SerializedName("data")
                    val `data`: Data
                ) : Parcelable {
                    @Parcelize
                    data class Data(
                        @SerializedName("status")
                        val status: Boolean
                    ) : Parcelable
                }

                @Parcelize
                data class UserWantsToWatch(
                    @SerializedName("data")
                    val `data`: Data
                ) : Parcelable {
                    @Parcelize
                    data class Data(
                        @SerializedName("status")
                        val status: Boolean
                    ) : Parcelable
                }

                @Parcelize
                data class UserWatch(
                    @SerializedName("data")
                    val `data`: Data
                ) : Parcelable {
                    @Parcelize
                    data class Data(
                        @SerializedName("duration")
                        val duration: Int,
                        @SerializedName("episode")
                        val episode: Any,
                        @SerializedName("language")
                        val language: String,
                        @SerializedName("progress")
                        val progress: Int,
                        @SerializedName("quality")
                        val quality: String,
                        @SerializedName("season")
                        val season: Any,
                        @SerializedName("updateDate")
                        val updateDate: String,
                        @SerializedName("visible")
                        val visible: Boolean,
                        @SerializedName("watched")
                        val watched: Boolean
                    ) : Parcelable
                }
            }
        }
    }
}