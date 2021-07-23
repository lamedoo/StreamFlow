package com.lukakordzaia.streamflow.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingDao
import com.lukakordzaia.streamflow.database.continuewatchingdb.ContinueWatchingRoom

@Database(entities = [ContinueWatchingRoom::class], version = 2)
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