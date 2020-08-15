@file:UseSerializers(LocalDateSerializer::class, LocalDateTimeSerializer::class)
package com.caldeirasoft.castly.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.caldeirasoft.castly.data.features.serializers.LocalDateSerializer
import com.caldeirasoft.castly.data.features.serializers.LocalDateTimeSerializer
import com.caldeirasoft.castly.domain.model.entities.Artwork
import com.caldeirasoft.castly.domain.model.entities.Episode
import com.caldeirasoft.castly.domain.model.entities.Genre
import com.caldeirasoft.castly.domain.model.entities.Podcast
import com.caldeirasoft.castly.domain.model.itunes.PodcastItunes
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.util.*

/**
 * Created by Edmond on 09/02/2018.
 */
@Entity(tableName = "podcasts")
@Parcelize
@Serializable
data class PodcastEntity(@PrimaryKey override var id: Long,
                         override var name: String = "",
                         override var artistName: String = "",
                         override var artistId: Long? = 0,
                         override var description: String? = null,
                         override var feedUrl: String = "",
                         override var releaseDate: LocalDate = LocalDate.MIN,
                         override var releaseDateTime: LocalDateTime = LocalDateTime.MIN,
                         override var podcastWebsiteUrl: String = "",
                         override var copyright: String = "",
                         override var contentAdvisoryRating: String = "", //🅴
                         override var trackCount: Int = 0,
                         override var artwork: Artwork? = null,
                         override var userRating: Float = 0f,

                         override var isSubscribed: Boolean = false,
                         override var subscribeAction: Int = 0, // queue,inbox,addfirst,addlast

                         override var genres: List<Genre> = emptyList(),
                         override var podcastsByArtist: List<Long> = emptyList(),
                         override var podcastsListenersAlsoFollow: List<Long> = emptyList()
) : Podcast() {

    @IgnoredOnParcel
    override var localStatus: Int = 0

    @IgnoredOnParcel
    var timeCreated: LocalDate? = null

    @IgnoredOnParcel
    var timeUpdate: LocalDate? = null

    @Ignore
    @IgnoredOnParcel
    override var episodes: List<Episode> = emptyList()

    /**
     * update values from a PodcastItunes object
     */
    fun updateFromItunes(podcastItunes: PodcastItunes) {
        id = podcastItunes.id
        genres = podcastItunes.genres
        description = podcastItunes.description
        artistName = podcastItunes.artistName
        artwork = podcastItunes.artwork
        name = podcastItunes.name
        userRating = podcastItunes.userRating
        feedUrl = podcastItunes.feedUrl
        releaseDateTime = podcastItunes.releaseDateTime
        releaseDate = podcastItunes.releaseDate
        trackCount = podcastItunes.trackCount
        artistId = podcastItunes.artistId
        contentAdvisoryRating = podcastItunes.contentAdvisoryRating
        copyright = podcastItunes.copyright
        podcastsByArtist = podcastItunes.podcastsByArtist
        podcastsListenersAlsoFollow = podcastItunes.podcastsListenersAlsoFollow
        podcastWebsiteUrl = podcastItunes.podcastWebsiteUrl
    }
}