package com.lukakordzaia.core.database.continuewatchingdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ContinueWatchingRoom (
    @PrimaryKey
    var titleId: Int,
    @ColumnInfo(name = "language")
    var language: String,
    @ColumnInfo(name = "watchedDuration")
    var watchedDuration: Long,
    @ColumnInfo(name = "titleDuration")
    var titleDuration: Long,
    @ColumnInfo(name = "isTvShow")
    var isTvShow: Boolean,
    @ColumnInfo(name = "season")
    var season: Int,
    @ColumnInfo(name = "episode")
    var episode: Int
)