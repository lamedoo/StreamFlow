package com.lukakordzaia.streamflow.datamodels


import com.google.gson.annotations.SerializedName

data class StudioList(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("meta")
    val meta: Meta
) {
    data class Data(
        @SerializedName("backgroundImage")
        val backgroundImage: String,
        @SerializedName("backgroundImageNarrow")
        val backgroundImageNarrow: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("isFeatured")
        val isFeatured: Boolean,
        @SerializedName("lightPoster")
        val lightPoster: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("poster")
        val poster: String,
        @SerializedName("rating")
        val rating: Double
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
            data class Links(
                @SerializedName("next")
                val next: String
            )
        }
    }
}