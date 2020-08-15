package com.caldeirasoft.castly.domain.model.itunes

import com.caldeirasoft.castly.domain.model.entities.Artwork
import com.caldeirasoft.castly.domain.model.entities.Podcast

/**
 * Created by Edmond on 09/02/2018.
 */
class RoomPageData(
        var pageTitle: String,
        var id: Long,
        var url: String,
        var artwork: Artwork?)
    : ArrayList<Podcast>() {
    val mapPodcastLookup: HashMap<Long, Podcast> = hashMapOf()
    var description: String? = null
    var ids: List<Long> = arrayListOf()

    constructor() : this("", 0, "", null)

    // retourne vrai s'il y a autant de podcast chargés que d'ids
    val isCollectionLoaded: Boolean
        get() = this.size == ids.size

    fun build() {
        ids.mapNotNull { id -> mapPodcastLookup[id] }
                .let { addAll(it) }
    }
}