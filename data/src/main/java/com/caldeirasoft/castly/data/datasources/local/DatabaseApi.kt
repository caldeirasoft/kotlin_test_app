package com.caldeirasoft.castly.data.datasources.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.caldeirasoft.castly.data.datasources.local.converters.DateTimeTextConverter
import com.caldeirasoft.castly.data.datasources.local.converters.DbTypeConverter
import com.caldeirasoft.castly.data.datasources.local.dao.EpisodeDao
import com.caldeirasoft.castly.data.datasources.local.dao.PodcastDao
import com.caldeirasoft.castly.data.entity.EpisodeEntity
import com.caldeirasoft.castly.data.entity.PodcastEntity

@Database(entities = arrayOf(PodcastEntity::class, EpisodeEntity::class), version = 2)
@TypeConverters(DbTypeConverter::class, DateTimeTextConverter::class)
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