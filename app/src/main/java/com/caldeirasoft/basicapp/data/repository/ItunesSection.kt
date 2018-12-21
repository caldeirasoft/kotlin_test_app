package com.caldeirasoft.basicapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.caldeirasoft.basicapp.data.entity.Podcast
import java.util.concurrent.Executors

/**
 * Created by Edmond on 09/02/2018.
 */
class ItunesSection(var name:String,
                    var ids: List<Int>) {
    val podcasts: LiveData<PagedList<Podcast>>
        get() {
            val podcastRepository = PodcastRepository()
            val ioExecutor = Executors.newFixedThreadPool(5)
            val sourceFactory = podcastRepository.getItunesLookupDataSourceFactory(ids)

            val pagedListConfig = PagedList.Config.Builder()
                    .setPageSize(PAGE_SIZE)
                    .setEnablePlaceholders(true)
                    .setPrefetchDistance(5)
                    .setInitialLoadSizeHint(40)
                    .build()
            val data =  LivePagedListBuilder(sourceFactory, pagedListConfig)
                    .setFetchExecutor(ioExecutor)
                    .build()
            return data
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ItunesSection
        if (!name.equals(other.name)) return false
        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    companion object {
        val PAGE_SIZE = 15
    }
}