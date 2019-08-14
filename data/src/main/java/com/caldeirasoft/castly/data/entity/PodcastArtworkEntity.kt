package com.caldeirasoft.castly.data.entity

import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.model.itunes.PodcastArtwork

/**
 * Created by Edmond on 09/02/2018.
 */
class PodcastArtworkEntity(override var podcast: Podcast) : PodcastArtwork {
    override var artworkUrl: String = ""
    override var bgColor: Int = 0
    override var textColor: Int = 0
}