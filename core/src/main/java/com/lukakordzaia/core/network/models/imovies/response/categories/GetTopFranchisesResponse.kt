package com.lukakordzaia.core.network.models.imovies.response.categories


import com.google.gson.annotations.SerializedName

data class GetTopFranchisesResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("meta")
    val meta: Meta
) {
    data class Data(
        @SerializedName("color")
        val color: String,
        @SerializedName("createDate")
        val createDate: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("followersCount")
        val followersCount: FollowersCount,
        @SerializedName("id")
        val id: Int,
        @SerializedName("imageUrl")
        val imageUrl: String,
        @SerializedName("isFeatured")
        val isFeatured: Boolean,
        @SerializedName("moviesCount")
        val moviesCount: MoviesCount,
        @SerializedName("name")
        val name: String,
        @SerializedName("posters")
        val posters: Posters,
        @SerializedName("private")
        val `private`: Boolean,
        @SerializedName("user")
        val user: User
    ) {
        data class FollowersCount(
            @SerializedName("data")
            val `data`: FollowersData
        ) {
            data class FollowersData(
                @SerializedName("count")
                val count: Int
            )
        }

        data class MoviesCount(
            @SerializedName("data")
            val `data`: MoviesCount
        ) {
            data class MoviesCount(
                @SerializedName("count")
                val count: Int
            )
        }

        data class Posters(
            @SerializedName("data")
            val `data`: List<PostersData>
        ) {
            data class PostersData(
                @SerializedName("cover")
                val cover: Cover,
                @SerializedName("covers")
                val covers: Covers,
                @SerializedName("poster")
                val poster: String,
                @SerializedName("posters")
                val posters: Posters
            ) {
                data class Cover(
                    @SerializedName("large")
                    val large: String,
                    @SerializedName("small")
                    val small: String
                )

                data class Covers(
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
                )

                data class Posters(
                    @SerializedName("blurhash")
                    val blurhash: String,
                    @SerializedName("240")
                    val x240: String
                )
            }
        }

        data class User(
            @SerializedName("data")
            val `data`: UsersData
        ) {
            data class UsersData(
                @SerializedName("avatar")
                val avatar: Avatar,
                @SerializedName("bio")
                val bio: Any,
                @SerializedName("birthDate")
                val birthDate: String,
                @SerializedName("canComment")
                val canComment: Int,
                @SerializedName("displayName")
                val displayName: String,
                @SerializedName("email")
                val email: String,
                @SerializedName("fbUserId")
                val fbUserId: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("privacy")
                val privacy: Boolean,
                @SerializedName("role")
                val role: String
            ) {
                data class Avatar(
                    @SerializedName("large")
                    val large: String,
                    @SerializedName("small")
                    val small: String
                )
            }
        }
    }

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