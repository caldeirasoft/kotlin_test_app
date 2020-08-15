package com.caldeirasoft.castly.domain.model.itunes

import com.caldeirasoft.castly.domain.model.entities.Artwork
import com.caldeirasoft.castly.domain.model.entities.Episode
import com.caldeirasoft.castly.domain.model.entities.Genre
import com.caldeirasoft.castly.domain.model.entities.Podcast
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

/**
 * Created by Edmond on 09/02/2018.
 */
@Parcelize
open class PodcastItunes(
        override var id: Long,
        override var name: String,
        override var artistName: String,
        override var artistId: Long?,
        override var genres: List<Genre>,
        override var description: String?,
        override var feedUrl: String,
        override var releaseDate: LocalDate,
        override var releaseDateTime: LocalDateTime,
        override var artwork: Artwork?,
        override var trackCount: Int,
        override var podcastWebsiteUrl: String,
        override var copyright: String,
        override var contentAdvisoryRating: String, //🅴
        override var userRating: Float
) : Podcast() {
    constructor() : this(
            0,
            "",
            "",
            null,
            arrayListOf(),
            null,
            "",
            LocalDate.MIN,
            LocalDateTime.MIN,
            Artwork("", 0, 0),
            0,
            "",
            "",
            "",
            0f
    )

    // collections
    override var episodes: List<Episode> = arrayListOf()
    override var podcastsByArtist: List<Long> = arrayListOf()
    override var podcastsListenersAlsoFollow: List<Long> = arrayListOf()

    override var isSubscribed: Boolean = false
    override var subscribeAction: Int = 0
    override var localStatus: Int = 0
}