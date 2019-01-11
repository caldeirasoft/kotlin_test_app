package com.caldeirasoft.basicapp.ui.podcast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import com.caldeirasoft.basicapp.Mockup
import com.caldeirasoft.basicapp.data.db.podcasts.PodcastDataSource
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.repository.PodcastRepository
import com.caldeirasoft.basicapp.ui.common.SingleLiveEvent
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class PodcastViewModel : ViewModel()
{
    var podcastRepository: PodcastDataSource
    var podcasts: LiveData<List<Podcast>>

    init {
        podcastRepository = PodcastRepository()
        podcasts = podcastRepository.getPodcastsFromDb()
    }

    companion object {
        val PAGE_SIZE = 15
    }
}