package com.lukakordzaia.streamflow.network.models.trakttv.response


import com.google.gson.annotations.SerializedName

class GetListByTitleTypeResponse : ArrayList<GetListByTitleTypeResponse.GetTraktSfListByTypeItem>(){
    data class GetTraktSfListByTypeItem(
        @SerializedName("episode")
        val episode: Episode,
        @SerializedName("id")
        val id: Int,
        @SerializedName("listed_at")
        val listedAt: String,
        @SerializedName("movie")
        val movie: Movie,
        @SerializedName("person")
        val person: Person,
        @SerializedName("rank")
        val rank: Int,
        @SerializedName("season")
        val season: Season,
        @SerializedName("show")
        val show: Show,
        @SerializedName("type")
        val type: String
    ) {
        data class Episode(
            @SerializedName("ids")
            val ids: Ids,
            @SerializedName("number")
            val number: Int,
            @SerializedName("season")
            val season: Int,
            @SerializedName("title")
            val title: String
        ) {
            data class Ids(
                @SerializedName("imdb")
                val imdb: Any,
                @SerializedName("tmdb")
                val tmdb: Int,
                @SerializedName("trakt")
                val trakt: Int,
                @SerializedName("tvdb")
                val tvdb: Int
            )
        }
    
        data class Movie(
            @SerializedName("ids")
            val ids: Ids,
            @SerializedName("title")
            val title: String,
            @SerializedName("year")
            val year: Int
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
    
        data class Person(
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
            val ids: Ids,
            @SerializedName("number")
            val number: Int
        ) {
            data class Ids(
                @SerializedName("tmdb")
                val tmdb: Int,
                @SerializedName("tvdb")
                val tvdb: Int
            )
        }
    
        data class Show(
            @SerializedName("ids")
            val ids: Ids,
            @SerializedName("title")
            val title: String,
            @SerializedName("year")
            val year: Int
        ) {
            data class Ids(
                @SerializedName("imdb")
                val imdb: String,
                @SerializedName("slug")
                val slug: String,
                @SerializedName("tmdb")
                val tmdb: Int,
                @SerializedName("trakt")
                val trakt: Int,
                @SerializedName("tvdb")
                val tvdb: Int
            )
        }
    }
}