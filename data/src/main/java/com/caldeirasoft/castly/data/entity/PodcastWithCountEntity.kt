package com.caldeirasoft.castly.domain.model


/**
 * Created by Edmond on 09/02/2018.
 */
class PodcastWithCountEntity : PodcastWithCount {
    override var id: Long = 0L
    override var title: String = ""
    override var artwork: String = ""
    override var episodeCount: Int = 0
}
