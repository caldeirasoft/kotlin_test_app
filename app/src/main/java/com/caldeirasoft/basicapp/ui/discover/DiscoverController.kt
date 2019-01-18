package com.caldeirasoft.basicapp.ui.discover

import com.airbnb.epoxy.TypedEpoxyController
import com.caldeirasoft.basicapp.*
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.repository.ItunesStore
import com.caldeirasoft.basicapp.ui.common.BaseBindingViewModel
import com.caldeirasoft.basicapp.ui.epoxy.carousel
import com.caldeirasoft.basicapp.ui.epoxy.withModelsFrom


// inspired from https://github.com/vipulyaara/Foodie -> /app/src/main/java/com/foodie/consumer/feature/detail/VenueDetailController.kt

class DiscoverController(private val callbacks: Callbacks) : TypedEpoxyController<ItunesStore>()
{
    interface Callbacks {
        fun onPodcastClick(podcast: Podcast)
    }

    override fun buildModels(data: ItunesStore?) {
        /*
        // add loader
        if (data?.isLoading == true && data?.sections.isEmpty()) {
            itemLoader { id("loader") }
        }

        // add empty layout
        if (data?.isLoading == false && data.venueDetail.isEmpty()) {
            itemEmptyState { id("empty-state") }
        }
        */

        // add trending view
        data?.let { store ->
            /*
            itemRowHeader {
                    id("favorite header")
                    text("Favorites")
                }
             */
            headerTrending {
                id("trending_header")
                text("")
            }
            carousel {
                id("trending_content")
                withModelsFrom(store.trending) {
                    ItemPodcastTrendingBindingModel_()
                            .id("trending" + it.artworkUrl)
                            .imageUrl(it.artworkUrl)
                            .onPodcastClick { _ ->
                                callbacks.onPodcastClick(it.podcast)
                            }
                }
            }

            store.sections.forEach {section ->
                /*
                itemRowHeader {
                    id("favorite header")
                    text("Favorites")
                }
                */
                headerDiscoverSection {
                    id(section.name+"_header")
                    text(section.name)
                }
                carousel {
                    id(section.name+"_content")
                    withModelsFrom(section.podcasts) {
                       ItemPodcastDiscoverBindingModel_()
                                .id("section" + it.feedUrl)
                                .title(it.title)
                                .imageUrl(it.imageUrl)
                                .authors(it.authors)
                                .onPodcastClick { _ ->
                                    callbacks.onPodcastClick(it)
                                }
                    }
                }
            }
        }
    }
}