package com.caldeirasoft.castly.domain.model.entities


/**
 * Created by Edmond on 09/02/2018.
 */
interface PodcastWithCount {
    var id: Long
    var title: String
    var artwork: String
    var episodeCount: Int

    fun getArtwork(width: Int): String = Podcast.getArtwork(artwork, width)
}