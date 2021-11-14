package com.lukakordzaia.core.network.models.imovies.response.user


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetUserDataResponse(
    @SerializedName("data")
    val `data`: Data
) : Parcelable {
    @Parcelize
    data class Data(
        @SerializedName("avatar")
        val avatar: Avatar,
        @SerializedName("bio")
        val bio: String?,
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
        @SerializedName("followersCount")
        val followersCount: FollowersCount,
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("privacy")
        val privacy: Boolean,
        @SerializedName("role")
        val role: String,
        @SerializedName("settings")
        val settings: Settings,
        @SerializedName("username")
        val username: String,
        @SerializedName("verified")
        val verified: Boolean
    ) : Parcelable {
        @Parcelize
        data class Avatar(
            @SerializedName("large")
            val large: String,
            @SerializedName("small")
            val small: String
        ) : Parcelable

        @Parcelize
        data class FollowersCount(
            @SerializedName("data")
            val `data`: Data
        ) : Parcelable {
            @Parcelize
            data class Data(
                @SerializedName("count")
                val count: Int
            ) : Parcelable
        }

        @Parcelize
        data class Settings(
            @SerializedName("data")
            val `data`: Data
        ) : Parcelable {
            @Parcelize
            data class Data(
                @SerializedName("notifyComments")
                val notifyComments: NotifyComments,
                @SerializedName("notifyLists")
                val notifyLists: NotifyLists,
                @SerializedName("notifyMovieFile")
                val notifyMovieFile: NotifyMovieFile,
                @SerializedName("notifyMovieLanguages")
                val notifyMovieLanguages: NotifyMovieLanguages,
                @SerializedName("notifyProfile")
                val notifyProfile: NotifyProfile,
                @SerializedName("notifyTrailers")
                val notifyTrailers: NotifyTrailers,
                @SerializedName("theme")
                val theme: String
            ) : Parcelable {
                @Parcelize
                data class NotifyComments(
                    @SerializedName("database")
                    val database: Boolean,
                    @SerializedName("webpush")
                    val webpush: Boolean
                ) : Parcelable

                @Parcelize
                data class NotifyLists(
                    @SerializedName("database")
                    val database: Boolean,
                    @SerializedName("webpush")
                    val webpush: Boolean
                ) : Parcelable

                @Parcelize
                data class NotifyMovieFile(
                    @SerializedName("database")
                    val database: Boolean,
                    @SerializedName("webpush")
                    val webpush: Boolean
                ) : Parcelable

                @Parcelize
                data class NotifyMovieLanguages(
                    @SerializedName("ENG")
                    val eNG: Boolean,
                    @SerializedName("GEO")
                    val gEO: Boolean,
                    @SerializedName("RUS")
                    val rUS: Boolean
                ) : Parcelable

                @Parcelize
                data class NotifyProfile(
                    @SerializedName("database")
                    val database: Boolean,
                    @SerializedName("webpush")
                    val webpush: Boolean
                ) : Parcelable

                @Parcelize
                data class NotifyTrailers(
                    @SerializedName("database")
                    val database: Boolean,
                    @SerializedName("webpush")
                    val webpush: Boolean
                ) : Parcelable
            }
        }
    }
}