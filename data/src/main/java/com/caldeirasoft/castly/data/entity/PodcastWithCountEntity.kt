package com.caldeirasoft.castly.domain.model


/**
 * Created by Edmond on 09/02/2018.
 */
class PodcastWithCountEntity : PodcastWithCount {
    override var feedUrl: String = ""
    override var title: String = ""
    override var imageUrl: String? = null
    override var episodeCount: Int = 0

    override fun isPodcast():Boolean = feedUrl.startsWith("feed/")
}