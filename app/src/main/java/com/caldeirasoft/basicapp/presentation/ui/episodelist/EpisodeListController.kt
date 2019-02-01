package com.caldeirasoft.basicapp.presentation.ui.episodelist

import com.airbnb.epoxy.TypedEpoxyController
import com.caldeirasoft.basicapp.domain.entity.Episode
import com.caldeirasoft.basicapp.itemEpisode

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