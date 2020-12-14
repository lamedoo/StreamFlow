package com.lukakordzaia.imoviesapp.network.models


import com.google.gson.annotations.SerializedName

data class MovieFiles(
    @SerializedName("data")
    val `data`: List<Data>
) {
    data class Data(
        @SerializedName("covers")
        val covers: Covers?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("episode")
        val episode: Int?,
        @SerializedName("episodes_include")
        val episodesInclude: String?,
        @SerializedName("files")
        val files: List<File>?,
        @SerializedName("poster")
        val poster: String?,
        @SerializedName("title")
        val title: String?
    ) {
        data class Covers(
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

        data class File(
            @SerializedName("files")
            val files: List<File1>?,
            @SerializedName("lang")
            val lang: String?,
            @SerializedName("subtitles")
            val subtitles: List<Subtitle?>?
        ) {
            data class File1(
                @SerializedName("duration")
                val duration: Int?,
                @SerializedName("id")
                val id: Int?,
                @SerializedName("quality")
                val quality: String?,
                @SerializedName("src")
                val src: String?,
                @SerializedName("thumbnails")
                val thumbnails: List<Thumbnail?>?
            ) {
                data class Thumbnail(
                    @SerializedName("columns")
                    val columns: Int?,
                    @SerializedName("duration")
                    val duration: Int?,
                    @SerializedName("end_time")
                    val endTime: Int?,
                    @SerializedName("height")
                    val height: Int?,
                    @SerializedName("id")
                    val id: Int?,
                    @SerializedName("interval")
                    val interval: Int?,
                    @SerializedName("start_time")
                    val startTime: Int?,
                    @SerializedName("url")
                    val url: String?,
                    @SerializedName("width")
                    val width: Int?
                )
            }

            data class Subtitle(
                @SerializedName("lang")
                val lang: String?,
                @SerializedName("url")
                val url: String?
            )
        }
    }
}