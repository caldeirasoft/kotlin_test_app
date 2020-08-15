package com.caldeirasoft.castly.domain.model.itunes

import com.caldeirasoft.castly.domain.model.entities.Podcast

/**
 * Created by Edmond on 09/02/2018.
 */
class ArtistPageData()
    : ArrayList<PodcastCollection>() {

    var artist = ArtistItunes()
    var mapPodcastLookup: HashMap<Long, Podcast> = hashMapOf()
}