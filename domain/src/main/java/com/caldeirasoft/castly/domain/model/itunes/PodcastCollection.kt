package com.caldeirasoft.castly.domain.model.itunes

import com.caldeirasoft.castly.domain.model.entities.Podcast
import kotlinx.serialization.Serializable
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

/**
 * Created by Edmond on 09/02/2018.
 */
@Serializable
class PodcastCollection(
        var label: String,
        var id: Long,
        var ids: List<Long>
) : GroupingPageData.Collection {
    val items: ArrayList<Podcast> = arrayListOf()

    // retourne vrai s'il y a autant d'items chargés
    // que d'ids (dans la limite de [LIMIT])
    val isCollectionLoaded: Boolean
        get() = this.items.size == Math.min(ids.size, LIMIT)

    fun build(mapLookup: HashMap<Long, Podcast>) {
        ids.take(LIMIT)
                .mapNotNull { id -> mapLookup[id] }
                .let { items.addAll(it) }
    }

    companion object {
        const val LIMIT = 10
    }
}