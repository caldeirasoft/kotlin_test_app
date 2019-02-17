package com.caldeirasoft.castly.domain.model.itunes

import com.caldeirasoft.castly.domain.model.Podcast

/**
 * Created by Edmond on 09/02/2018.
 */
interface PodcastArtwork {
    var podcast: Podcast
    var artworkUrl: String
    var bgColor: Int
    var textColor: Int
}