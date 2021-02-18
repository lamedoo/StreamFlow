package com.lukakordzaia.streamflow.datamodels


import com.google.gson.annotations.SerializedName

data class TraktvNewList(
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