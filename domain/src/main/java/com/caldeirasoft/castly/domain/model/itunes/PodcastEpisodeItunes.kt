package com.caldeirasoft.castly.domain.model.itunes

import com.caldeirasoft.castly.domain.model.entities.Artwork
import com.caldeirasoft.castly.domain.model.entities.Episode
import com.caldeirasoft.castly.domain.model.entities.Genre
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

/**
 * Created by Edmond on 09/02/2018.
 */
@Parcelize
open class PodcastEpisodeItunes(
        override var id: Long,
        override var name: String,
        override var podcastName: String,
        override var podcastId: Long,
        override var artistName: String,
        override var artistId: Long?,
        override var genres: List<Genre>,
        override var releaseDate: LocalDate,
        override var releaseDateTime: LocalDateTime,
        override var feedUrl: String,
        override var description: String,
        override var contentAdvisoryRating: String, //🅴
        override var artwork: Artwork?,
        override var mediaUrl: String,
        override var mediaType: String,
        override var duration: Int,
        override var playbackPosition: Int?,

        override var podcastEpisodeSeason: Int? = null,
        override var podcastEpisodeNumber: Int? = null,
        override var podcastEpisodeWebsiteUrl: String? = null,
        override var podcastEpisodeType: String = "",

        override var section: Int = 0,
        override var queuePosition: Int? = null,
        override var isFavorite: Boolean = false,
        override var isPlayed: Boolean = false
) : Episode() {
    constructor() : this(
            0,
            "",
            "",
            0,
            "",
            null,
            arrayListOf(),
            LocalDate.MIN,
            LocalDateTime.MIN,
            "",
            "",
            "",
            Artwork("", 0, 0),
            "",
            "",
            0,
            null)

    override fun publishedFormat(): String = ""
    override fun durationFormat(): String? = null
}