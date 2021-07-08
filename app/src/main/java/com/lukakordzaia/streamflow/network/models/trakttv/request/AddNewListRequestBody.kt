package com.lukakordzaia.streamflow.network.models.trakttv.request


import com.google.gson.annotations.SerializedName

data class AddNewListRequestBody(
    @SerializedName("allow_comments")
    val allowComments: Boolean?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("display_numbers")
    val displayNumbers: Boolean?,
    @SerializedName("name")
    val name: String,
    @SerializedName("privacy")
    val privacy: String?,
    @SerializedName("sort_by")
    val sortBy: String?,
    @SerializedName("sort_how")
    val sortHow: String?
)