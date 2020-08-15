package com.caldeirasoft.castly.domain.model.itunes

import com.caldeirasoft.castly.domain.model.entities.Artwork
import com.caldeirasoft.castly.domain.model.entities.Podcast

/**
 * Created by Edmond on 09/02/2018.
 */
abstract class MultiRoomPageData(
        var pageTitle: String,
        var id: Long,
        var url: String,
        var artwork: Artwork?)
    : ArrayList<PodcastCollection>() {
    abstract var description: String?
    val mapPodcastLookup: HashMap<Long, Podcast> = hashMapOf()
}