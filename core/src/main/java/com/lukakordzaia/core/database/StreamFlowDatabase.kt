package com.lukakordzaia.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingDao
import com.lukakordzaia.core.database.continuewatchingdb.ContinueWatchingRoom

@Database(entities = [ContinueWatchingRoom::class], version = 1)
abstract class StreamFlowDatabase : RoomDatabase() {
    abstract fun continueWatchingDao(): ContinueWatchingDao

    companion object {
        private var database: StreamFlowDatabase? = null

        fun getDatabase(context: Context): StreamFlowDatabase? {
            database ?: kotlin.run {
                database = Room.databaseBuilder(context, StreamFlowDatabase::class.java, "StreamFlowDb")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return  database
        }
    }
}