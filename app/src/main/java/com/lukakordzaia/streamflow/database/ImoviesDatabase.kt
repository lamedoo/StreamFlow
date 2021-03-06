package com.lukakordzaia.streamflow.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DbDetails::class], version = 9)
abstract class ImoviesDatabase : RoomDatabase() {
    abstract fun getDao(): WatchedDao

    companion object {
        private var database: ImoviesDatabase? = null

        fun getDatabase(context: Context): ImoviesDatabase? {
            database ?: kotlin.run {
                database = Room.databaseBuilder(context, ImoviesDatabase::class.java, "dbTitles")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return  database
        }
    }
}