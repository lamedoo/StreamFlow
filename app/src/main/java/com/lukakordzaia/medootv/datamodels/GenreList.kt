package com.lukakordzaia.medootv.datamodels


import com.google.gson.annotations.SerializedName

data class GenreList(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("meta")
    val meta: Meta
) {
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
    )

    data class Meta(
        @SerializedName("pagination")
        val pagination: Pagination
    ) {
        data class Pagination(
            @SerializedName("count")
            val count: Int,
            @SerializedName("current_page")
            val currentPage: Int,
            @SerializedName("links")
            val links: Links,
            @SerializedName("per_page")
            val perPage: Int,
            @SerializedName("total")
            val total: Int,
            @SerializedName("total_pages")
            val totalPages: Int
        ) {
            class Links(
            )
        }
    }
}