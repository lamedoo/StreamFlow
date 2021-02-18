package com.lukakordzaia.streamflow.datamodels


import com.google.gson.annotations.SerializedName

data class TraktUserProfile(
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
    @SerializedName("vip_cover_image")
    val vipCoverImage: String,
    @SerializedName("vip_ep")
    val vipEp: Boolean,
    @SerializedName("vip_og")
    val vipOg: Boolean,
    @SerializedName("vip_years")
    val vipYears: Int
) {
    data class Ids(
        @SerializedName("slug")
        val slug: String
    )
}