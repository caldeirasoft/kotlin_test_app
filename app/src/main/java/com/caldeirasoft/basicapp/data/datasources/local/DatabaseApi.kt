package com.caldeirasoft.basicapp.data.datasources.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.caldeirasoft.basicapp.data.datasources.local.dao.EpisodeDao
import com.caldeirasoft.basicapp.data.datasources.local.dao.PodcastDao
import com.caldeirasoft.basicapp.domain.entity.Episode
import com.caldeirasoft.basicapp.domain.entity.Podcast

@Database(entities = arrayOf(Podcast::class, Episode::class), version = 2)
abstract class DatabaseApi : RoomDatabase() {
    abstract fun episodeDao(): EpisodeDao
    abstract fun podcastDao(): PodcastDao

    companion object {
        fun buildDatabase(context: Context): DatabaseApi {
            return Room.databaseBuilder(context, DatabaseApi::class.java, "app-db")
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }
}