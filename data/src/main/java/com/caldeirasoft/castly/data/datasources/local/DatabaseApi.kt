package com.caldeirasoft.castly.data.datasources.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.caldeirasoft.castly.data.datasources.local.dao.EpisodeDao
import com.caldeirasoft.castly.data.datasources.local.dao.PodcastDao
import com.caldeirasoft.castly.domain.model.EpisodeEntity
import com.caldeirasoft.castly.domain.model.PodcastEntity

@Database(entities = arrayOf(PodcastEntity::class, EpisodeEntity::class), version = 2)
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