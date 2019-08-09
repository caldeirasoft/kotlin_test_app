package com.caldeirasoft.basicapp.presentation.ui.discover

import android.view.View
import com.airbnb.epoxy.Carousel
import com.caldeirasoft.basicapp.*
import com.caldeirasoft.basicapp.presentation.utils.epoxy.BaseTypedController
import com.caldeirasoft.basicapp.presentation.utils.extensions.carousel
import com.caldeirasoft.basicapp.presentation.utils.extensions.withModelsFrom
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.model.itunes.StoreCollection
import com.caldeirasoft.castly.domain.model.itunes.StoreData
import com.caldeirasoft.castly.domain.model.itunes.StoreMultiCollection

class DiscoverExpoxyController(private val callbacks: Callbacks)
    : BaseTypedController<StoreData>() {
    interface Callbacks {
        fun onPodcastClicked(podcast: Podcast, view: View)
        fun onCollectionClicked(collection: StoreCollection, view: View)
    }

    override fun buildModels(data: StoreData?) {
        if (isLoading) {
            buildLoadingModel()
        }
        else if (isInErrorState)
            // Show error
        else {
            buildStoreDataModels(data)
        }
    }

    private fun buildLoadingModel() {
        loading {
            id("loading")
        }
    }

    private fun buildStoreDataModels(storeData: StoreData?) {
        val carouselPadding = Carousel.Padding.dp(8, 0, 8, 8, 4)
        val numViewsToShowOnScreen = 3.1f

        storeData?.groups?.forEach { group ->
            if (group is StoreCollection) {
                sectionTitle {
                    id(group.name + "_header")
                    sectionTitle(group.name)
                    //seeAllAction{ blablabla }
                }
                carousel {
                    id(group.name + "_content")
                    withModelsFrom(group.podcasts) {
                        PodcastSectionBindingModel_()
                                .id("section" + it.feedUrl)
                                .title(it.name)
                                .imageUrl(it.getArtwork(100))
                                .authors(it.artistName)
                                .transitionName(it.transitionName)
                                .clickListener { view -> callbacks.onPodcastClicked(it, view) }
                    }

                    numViewsToShowOnScreen(numViewsToShowOnScreen)
                    padding(carouselPadding)
                    hasFixedSize(true)
                }
            }
            else if (group is StoreMultiCollection) {
                group.let {
                    collectionTitle {
                        id("collection")
                        collectionTitle("collection")
                    }
                    carousel {
                        id("trending_content")
                        withModelsFrom(group.multiCollection) {
                            CollectionBindingModel_()
                                    .id("trending" + it.artworkUrl)
                                    .imageUrl(it.artworkUrl)
                                    .clickListener { view -> callbacks.onCollectionClicked(it, view) }
                        }
                    }
                }
            }
        }
    }
}