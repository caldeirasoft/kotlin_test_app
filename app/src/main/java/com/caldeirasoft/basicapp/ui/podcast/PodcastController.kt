package com.caldeirasoft.basicapp.ui.podcast

import com.airbnb.epoxy.TypedEpoxyController
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.podcastItem

class PodcastController(private val callbacks: Callbacks) : TypedEpoxyController<List<Podcast>>()
{
    interface Callbacks {
        fun onPodcastClick(podcast: Podcast)
    }

    override fun buildModels(data: List<Podcast>?) {
        data ?: return
        data.forEach { content ->
            podcastItem {
                id(content.feedUrl)
                title(content.title)
                imageUrl(content.imageUrl)
                onPodcastClick { _ -> callbacks.onPodcastClick(content) }
            }
        }
    }
}