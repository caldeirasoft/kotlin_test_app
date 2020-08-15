package com.caldeirasoft.basicapp.presentation.ui.discover

import android.view.View
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.Typed2EpoxyController
import com.airbnb.epoxy.Typed3EpoxyController
import com.airbnb.epoxy.TypedEpoxyController
import com.caldeirasoft.basicapp.*
import com.caldeirasoft.basicapp.presentation.utils.epoxy.BaseTypedController
import com.caldeirasoft.basicapp.presentation.utils.extensions.carousel
import com.caldeirasoft.basicapp.presentation.utils.extensions.withModelsFrom
import com.caldeirasoft.castly.domain.model.entities.Podcast
import com.caldeirasoft.castly.domain.model.itunes.GroupingPageData
import com.caldeirasoft.castly.domain.model.itunes.PodcastCollection
import com.caldeirasoft.castly.domain.util.Resource
import com.caldeirasoft.castly.domain.util.Status
import java.util.*

class DiscoverHeaderEpoxyController(
        val viewModel: DiscoverViewModel,
        val callbacks: Callbacks)
    : Typed2EpoxyController<List<GroupingPageData.TrendingItem>, Status?>() {
    interface Callbacks {
        fun onCollectionItemClicked(collection: GroupingPageData.TrendingItem, view: View)
    }

    private val modelId: String
        get() = UUID.randomUUID().toString()

    override fun buildModels(collection: List<GroupingPageData.TrendingItem>, status: Status?) {
        when {
            (status == null || status == Status.LOADING) && collection.isEmpty() ->
                buildLoadingModel()
            else -> buildDataModels(collection)
        }
    }

    private fun buildLoadingModel() {
        loading {
            id(modelId)
        }
    }

    private fun buildDataModels(collection: List<GroupingPageData.TrendingItem>) {
        collection.forEach { collectionItem ->
            discoverHeaderItem {
                // epoxy_discover_header_item
                id("header_${collectionItem.id}")
                item(collectionItem)
                onItemClick { view ->
                    callbacks.onCollectionItemClicked(collectionItem, view)
                }
            }
        }
    }
}
