package com.lukakordzaia.imoviesapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WatchedDetails (
    @PrimaryKey
    var titleId: Int,
    @ColumnInfo(name = "watchedTime")
    var watchedTime: Long,
    @ColumnInfo(name = "isTvShow")
    var isTvShow: Boolean,
    @ColumnInfo(name = "movieLink")
    var movieLink: String,
    @ColumnInfo(name = "season")
    var season: Int,
    @ColumnInfo(name = "episode")
    var episode: Int
)