package com.caldeirasoft.basicapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.caldeirasoft.basicapp.data.db.episodes.EpisodeDao
import com.caldeirasoft.basicapp.data.db.podcasts.PodcastDao
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast

@Database(entities = arrayOf(Podcast::class, Episode::class), version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun episodeDao(): EpisodeDao
    abstract fun podcastDao(): PodcastDao
}