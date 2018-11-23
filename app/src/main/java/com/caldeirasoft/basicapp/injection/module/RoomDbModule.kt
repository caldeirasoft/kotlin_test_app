package com.caldeirasoft.basicapp.injection.module

import android.content.Context
import androidx.room.Room
import com.caldeirasoft.basicapp.api.feedly.retrofit.FeedlyAPI
import com.caldeirasoft.basicapp.api.itunes.retrofit.ITunesAPI
import com.caldeirasoft.basicapp.data.db.AppDatabase
import com.caldeirasoft.basicapp.data.db.episodes.EpisodeDao
import com.caldeirasoft.basicapp.data.db.podcasts.PodcastDao
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.github.salomonbrys.kodein.*

class RoomDbModule
{
    val bind = Kodein.Module {
        bind<AppDatabase>() with singleton { provideRoom(instance<Context>(), "db_name") }
    }

    fun provideRoom(context:Context, dbName:String): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, dbName)
                    .fallbackToDestructiveMigration()
                    .build()
}
