package com.caldeirasoft.basicapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.caldeirasoft.basicapp.data.entity.Podcast
import java.util.concurrent.Executors

/**
 * Created by Edmond on 09/02/2018.
 */
data class ItunesStore(
        var trending: List<PodcastArtwork> = arrayListOf(),
        var sections: List<ItunesSection> = arrayListOf(),
        var isLoading: Boolean = true
) {
    companion object {
        fun default() = ItunesStore()
    }
}