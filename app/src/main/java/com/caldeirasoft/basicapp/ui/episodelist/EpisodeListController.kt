package com.caldeirasoft.basicapp.ui.episodelist

import android.view.View
import com.airbnb.epoxy.TypedEpoxyController
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.itemEpisode
import com.caldeirasoft.basicapp.itemPodcast

class EpisodeListController(private val callbacks: Callbacks) : TypedEpoxyController<List<Episode>>()
{
    interface Callbacks {
        fun onEpisodeClick(episode: Episode)
    }

    override fun buildModels(data: List<Episode>?) {
        data ?: return
        data.forEach { content ->
            itemEpisode {
                id(content.feedUrl)
                title(content.title)
                imageUrl(content.imageUrl)
                podcastTitle(content.podcastTitle)
                onEpisodeClick { model, parentView, clickedView, position ->
                    callbacks.onEpisodeClick(content)
                }
            }
        }

        //TODO: empty
    }
}