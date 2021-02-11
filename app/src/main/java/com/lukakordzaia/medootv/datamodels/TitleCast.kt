package com.lukakordzaia.medootv.datamodels


import com.google.gson.annotations.SerializedName

data class TitleCast(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("meta")
    val meta: Meta
) {
    data class Data(
        @SerializedName("birthDate")
        val birthDate: String,
        @SerializedName("birthPlace")
        val birthPlace: String,
        @SerializedName("deathDate")
        val deathDate: String,
        @SerializedName("deathPlace")
        val deathPlace: String,
        @SerializedName("height")
        val height: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("originalName")
        val originalName: String,
        @SerializedName("personRole")
        val personRole: PersonRole,
        @SerializedName("poster")
        val poster: String,
        @SerializedName("primaryName")
        val primaryName: String,
        @SerializedName("secondaryName")
        val secondaryName: String,
        @SerializedName("slogan")
        val slogan: String,
        @SerializedName("tertiaryName")
        val tertiaryName: String,
        @SerializedName("userFollows")
        val userFollows: UserFollows?,
        @SerializedName("zodiacSign")
        val zodiacSign: String?
    ) {
        data class PersonRole(
            @SerializedName("data")
            val `data`: Data
        ) {
            data class Data(
                @SerializedName("character")
                val character: String?,
                @SerializedName("role")
                val role: String
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