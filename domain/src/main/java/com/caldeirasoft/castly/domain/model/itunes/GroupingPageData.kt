package com.caldeirasoft.castly.domain.model.itunes

import com.caldeirasoft.castly.domain.model.entities.Artwork
import com.caldeirasoft.castly.domain.model.entities.Podcast
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

/**
 * Created by Edmond on 09/02/2018.
 */
@Serializable
abstract class GroupingPageData {
    abstract val items: ArrayList<Collection>

    interface TrendingItem {
        val label: String
        val id: Long
        val url: String
        val artwork: Artwork
    }

    @Polymorphic
    interface Collection

    @Polymorphic
    interface TrendingCollection : Collection {
        var label: String
        var id: Long
        var isHeader: Boolean
        val items: ArrayList<TrendingItem>
    }

}