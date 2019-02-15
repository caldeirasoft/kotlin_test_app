package com.caldeirasoft.castly.domain.model


/**
 * Created by Edmond on 09/02/2018.
 */
interface PodcastWithCount {
    var feedUrl: String
    var title: String
    var imageUrl: String?
    var episodeCount: Int

    fun isPodcast():Boolean
}