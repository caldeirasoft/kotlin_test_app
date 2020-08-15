package com.caldeirasoft.basicapp.presentation.ui.discover

import android.view.View
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.Typed3EpoxyController
import com.caldeirasoft.basicapp.*
import com.caldeirasoft.basicapp.presentation.utils.extensions.carousel
import com.caldeirasoft.basicapp.presentation.utils.extensions.withModelsFrom
import com.caldeirasoft.castly.domain.model.entities.Podcast
import com.caldeirasoft.castly.domain.model.itunes.GroupingPageData
import com.caldeirasoft.castly.domain.model.itunes.PodcastCollection
import com.caldeirasoft.castly.domain.util.Status
import java.util.*

class DiscoverEpoxyController(
        val viewModel: DiscoverViewModel,
        private val callbacks: Callbacks)
    : Typed3EpoxyController<GroupingPageData?, Status?, String?>() {
    interface Callbacks {
        fun onPodcastClicked(podcast: Podcast, view: View)
        fun onCollectionItemClicked(collection: GroupingPageData.TrendingItem, view: View)
    }

    private val modelId: String
        get() = UUID.randomUUID().toString()

    override fun buildModels(data: GroupingPageData?, status: Status?, errorMessage: String?) {
        when (status) {
            null, Status.LOADING ->
                if ((data != null) && (data.items.isEmpty()))
                    buildDiscoverLoadingModel()
                else
                    buildLoadingModel()
            Status.ERROR ->
                buildErrorModel()
            else -> buildStoreDataModels(data)
        }
    }

    private fun buildLoadingModel() {
        loading {
            id(modelId)
        }
    }

    private fun buildDiscoverLoadingModel() {
        (1..20).forEach { _ ->
            discoverLoading {
                id(modelId)
            }
        }
    }

    private fun buildErrorModel() {
        layoutError {
            id(modelId)
        }
    }

    private fun buildStoreDataModels(storeData: GroupingPageData?) {
        val carouselPadding = Carousel.Padding.dp(8, 0, 8, 8, 4)
        val numViewsToShowOnScreen = 3.1f

        storeData?.items?.forEach { group ->
            when (group) {
                is PodcastCollection -> {
                    sectionTitle {
                        id("${group.id}_header")
                        sectionTitle(group.label)
                        //seeAllAction{ blablabla }
                    }
                    carousel {
                        id("${group.id}_content")
                        withModelsFrom(group.items) {
                            PodcastSectionBindingModel_()
                                    .id("podcast_${it.id}_${group.id}")
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
                is GroupingPageData.TrendingCollection -> {
                    if (!group.isHeader || true) {
                        if (group.label.isNotEmpty()) {
                            discoverCollectionTitle {
                                id("${group.id}_header")
                                collectionTitle(group.label)
                            }
                        }
                        carousel {
                            id("${group.id}_content")
                            withModelsFrom(group.items) {
                                DiscoverCollectionItemBindingModel_()
                                        .id("trending_${it.id}_${group.id}")
                                        .item(it)
                                        .onItemClick { view -> callbacks.onCollectionItemClicked(it, view) }
                            }
                        }
                    }
                }

            }
        }
    }
}
