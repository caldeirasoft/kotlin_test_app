package com.caldeirasoft.basicapp.presentation.ui.podcast

import android.view.View
import com.airbnb.epoxy.TypedEpoxyController
import com.caldeirasoft.basicapp.domain.entity.Podcast
import com.caldeirasoft.basicapp.itemPodcast

class PodcastController(private val callbacks: Callbacks) : TypedEpoxyController<List<Podcast>>()
{
    interface Callbacks {
        fun onPodcastClick(view: View, podcast: Podcast)
    }

    override fun buildModels(data: List<Podcast>?) {
        data ?: return
        data.forEach { content ->
            itemPodcast {
                id(content.feedUrl)
                title(content.title)
                imageUrl(content.imageUrl)
                onPodcastClick { model, parentView, clickedView, position ->
                    callbacks.onPodcastClick(clickedView, content)
                }
            }
        }

        //TODO: empty
    }
}