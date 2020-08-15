package com.caldeirasoft.castly.domain.model.entities

import android.os.Parcelable
import kotlinx.serialization.Serializable
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

@Serializable
abstract class Episode : Parcelable {
    abstract var id: Long
    abstract var name: String
    abstract var podcastId: Long
    abstract var podcastName: String
    abstract var artistName: String
    abstract var artistId: Long?
    abstract var releaseDate: LocalDate
    abstract var releaseDateTime: LocalDateTime
    abstract var genres: List<Genre>
    abstract var feedUrl: String
    abstract var description: String
    abstract var contentAdvisoryRating: String //🅴
    abstract var artwork: Artwork?
    abstract var mediaUrl: String
    abstract var mediaType: String
    abstract var duration: Int
    abstract var playbackPosition: Int?

    abstract var podcastEpisodeSeason: Int?
    abstract var podcastEpisodeNumber: Int?
    abstract var podcastEpisodeWebsiteUrl: String?
    abstract var podcastEpisodeType: String  //full/trailer/bonus

    abstract var section: Int
    abstract var queuePosition: Int?
    abstract var isFavorite: Boolean
    abstract var isPlayed: Boolean

    abstract fun publishedFormat(): String

    abstract fun durationFormat(): String?

    fun getArtwork(width: Int): String = Podcast.getArtwork(artwork?.url.orEmpty(), width)
}