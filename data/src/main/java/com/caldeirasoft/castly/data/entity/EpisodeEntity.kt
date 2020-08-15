@file:UseSerializers(LocalDateSerializer::class, LocalDateTimeSerializer::class)
package com.caldeirasoft.castly.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.caldeirasoft.castly.data.datasources.local.converters.DbTypeConverter
import com.caldeirasoft.castly.data.features.serializers.LocalDateSerializer
import com.caldeirasoft.castly.data.features.serializers.LocalDateTimeSerializer
import com.caldeirasoft.castly.domain.model.entities.Artwork
import com.caldeirasoft.castly.domain.model.entities.Episode
import com.caldeirasoft.castly.domain.model.entities.Genre
import com.caldeirasoft.castly.domain.model.entities.SectionState
import com.caldeirasoft.castly.domain.model.itunes.PodcastEpisodeItunes
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

/**
 * Created by Edmond on 09/02/2018.
 */
@Entity(tableName = "episodes",
        foreignKeys = arrayOf(ForeignKey(entity = PodcastEntity::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("podcastId"),
                onDelete = CASCADE))
)
@TypeConverters(DbTypeConverter::class)
@Parcelize
@Serializable
data class EpisodeEntity constructor(
        @PrimaryKey override var id: Long,
        override var name: String = "",
        override var artistId: Long? = null,
        override var artistName: String = "",
        override var podcastId: Long = 0,
        override var podcastName: String = "",
        override var releaseDate: LocalDate = LocalDate.MIN,
        override var releaseDateTime: LocalDateTime = LocalDateTime.MIN,
        override var feedUrl: String = "",
        override var description: String = "",
        override var artwork: Artwork? = null,
        override var contentAdvisoryRating: String = "", //🅴
        override var genres: List<Genre> = emptyList(),

        override var podcastEpisodeSeason: Int? = null,
        override var podcastEpisodeNumber: Int? = null,
        override var podcastEpisodeWebsiteUrl: String? = "",
        override var podcastEpisodeType: String = "", //full/trailer/bonus

        override var mediaUrl: String = "",
        override var mediaType: String = "",
        override var duration: Int = 0,
        override var playbackPosition: Int? = null,

        override var section: Int = SectionState.ARCHIVE.value, // queue/inbox/archive
        override var queuePosition: Int? = null,
        override var isFavorite: Boolean = false,
        override var isPlayed: Boolean = false
) : Episode() {

    //@get:com.google.firebase.firestore.Exclude
    var localStatus: Int = 0

    //@get:com.google.firebase.firestore.ServerTimestamp
    var timeCreated: LocalDate? = null

    //@get:com.google.firebase.firestore.ServerTimestamp
    var timeUpdate: Long? = null

    //@get:com.google.firebase.firestore.ServerTimestamp
    var timePlayed: Long? = null

    override fun publishedFormat(): String {
        return this.releaseDate.format(DateTimeFormatter.ofPattern("d/m/yyyy"))
    }

    override fun durationFormat(): String? =
            duration.let {
                Instant.ofEpochSecond(it.toLong()).let {
                    it.atZone(ZoneOffset.UTC).toLocalDateTime().let {
                        return String.format("%sm%s", it.minute.toString(), it.second.toString())
                    }
                }
            }

    /**
     * update values from a podcastEpisodeItunes object
     */
    fun updateFromItunes(podcastEpisodeItunes: PodcastEpisodeItunes) {
        id = podcastEpisodeItunes.id
        genres = podcastEpisodeItunes.genres
        description = podcastEpisodeItunes.description
        artistName = podcastEpisodeItunes.artistName
        artistId = podcastEpisodeItunes.artistId
        podcastName = podcastEpisodeItunes.podcastName
        podcastId = podcastEpisodeItunes.podcastId
        artwork = podcastEpisodeItunes.artwork
        name = podcastEpisodeItunes.name
        feedUrl = podcastEpisodeItunes.feedUrl
        releaseDateTime = podcastEpisodeItunes.releaseDateTime
        releaseDate = podcastEpisodeItunes.releaseDate
        contentAdvisoryRating = podcastEpisodeItunes.contentAdvisoryRating
        duration = podcastEpisodeItunes.duration
        genres = podcastEpisodeItunes.genres
        mediaType = podcastEpisodeItunes.mediaType
        mediaUrl = podcastEpisodeItunes.mediaUrl
        podcastEpisodeNumber = podcastEpisodeItunes.podcastEpisodeNumber
        podcastEpisodeSeason = podcastEpisodeItunes.podcastEpisodeSeason
        podcastEpisodeType = podcastEpisodeItunes.podcastEpisodeType
        podcastEpisodeWebsiteUrl = podcastEpisodeItunes.podcastEpisodeWebsiteUrl
    }
}