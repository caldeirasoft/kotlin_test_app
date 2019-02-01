package com.caldeirasoft.basicapp.presentation.ui.podcast

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.caldeirasoft.basicapp.domain.entity.Podcast
import com.caldeirasoft.basicapp.domain.repository.PodcastRepository

class PodcastViewModel(val podcastRepository: PodcastRepository) : ViewModel()
{
    var podcasts: LiveData<List<Podcast>> = podcastRepository.getPodcastsFromDb()

    companion object {
        val PAGE_SIZE = 15
    }
}