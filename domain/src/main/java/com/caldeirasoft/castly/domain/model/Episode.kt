package com.caldeirasoft.castly.domain.model

import android.os.Parcelable
import org.threeten.bp.LocalDateTime
import java.util.*

interface Episode : Parcelable
{
    var id: Long
    var name: String
    var artistName: String
    var podcastId: Long
    var podcastName: String
    var releaseDate: LocalDateTime

    var feedUrl: String
    var description: String
    var contentAdvisoryRating: String //🅴
    var artwork: String
    var artworkHeight: Int?
    var artworkWidth: Int?

    var podcastEpisodeSeason: Int?
    var podcastEpisodeNumber: Int?
    var podcastEpisodeWebsiteUrl: String?
    var podcastEpisodeType: String  //full/trailer/bonus

    var mediaUrl: String
    var mediaType: String?
    var mediaLength: Long?
    var duration: Int?
    var playbackPosition: Int?

    var section: Int
    var queuePosition: Int?
    var isFavorite: Boolean
    var isPlayed: Boolean

    //@get:com.google.firebase.firestore.Exclude
    var localStatus:Int
    //@get:com.google.firebase.firestore.ServerTimestamp
    var timeCreated: Date?
    //@get:com.google.firebase.firestore.ServerTimestamp
    var timeUpdate: Long?
    //@get:com.google.firebase.firestore.ServerTimestamp
    var timePlayed: Long?

    fun publishedFormat():String

    fun durationFormat(): String?

    fun getArtwork(width: Int): String = Podcast.getArtwork(artwork, width)
}