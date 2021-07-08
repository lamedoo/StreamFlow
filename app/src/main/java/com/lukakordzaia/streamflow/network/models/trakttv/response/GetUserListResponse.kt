package com.lukakordzaia.streamflow.network.models.trakttv.response


import com.google.gson.annotations.SerializedName

data class GetUserListResponse(
    @SerializedName("allow_comments")
    val allowComments: Boolean,
    @SerializedName("comment_count")
    val commentCount: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("display_numbers")
    val displayNumbers: Boolean,
    @SerializedName("ids")
    val ids: Ids,
    @SerializedName("item_count")
    val itemCount: Int,
    @SerializedName("likes")
    val likes: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("privacy")
    val privacy: String,
    @SerializedName("sort_by")
    val sortBy: String,
    @SerializedName("sort_how")
    val sortHow: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("user")
    val user: User
) {
    data class Ids(
        @SerializedName("slug")
        val slug: String,
        @SerializedName("trakt")
        val trakt: Int
    )

    data class User(
        @SerializedName("ids")
        val ids: Ids,
        @SerializedName("name")
        val name: String,
        @SerializedName("private")
        val `private`: Boolean,
        @SerializedName("username")
        val username: String,
        @SerializedName("vip")
        val vip: Boolean,
        @SerializedName("vip_ep")
        val vipEp: Boolean
    ) {
        data class Ids(
            @SerializedName("slug")
            val slug: String
        )
    }
}