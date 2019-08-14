package com.caldeirasoft.castly.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.caldeirasoft.castly.data.datasources.local.converters.DbTypeConverter
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.SectionState
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Edmond on 09/02/2018.
 */
@Entity(tableName = "episodes"
        ,foreignKeys = arrayOf(ForeignKey(entity = PodcastEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("podcastId"),
        onDelete = CASCADE))
)
@TypeConverters(DbTypeConverter::class)
@Parcelize
data class EpisodeEntity constructor(
        @PrimaryKey override var id: Long,
        override var name: String = "",
        override var artistName: String = "",
        override var podcastId: Long = 0,
        override var podcastName: String = "",
        override var releaseDate: LocalDateTime = LocalDateTime.MIN,

        override var feedUrl: String = "",
        override var description: String = "",
        override var artwork: String = "",
        override var artworkHeight: Int? = null,
        override var artworkWidth: Int? = null,
        override var contentAdvisoryRating: String = "", //🅴

        override var podcastEpisodeSeason: Int? = null,
        override var podcastEpisodeNumber: Int? = null,
        override var podcastEpisodeWebsiteUrl: String? = "",
        override var podcastEpisodeType: String = "", //full/trailer/bonus

        override var mediaUrl: String = "",
        override var mediaType: String? = null,
        override var mediaLength: Long? = null,
        override var duration: Int? = null,
        override var playbackPosition: Int? = null,

        override var section: Int = SectionState.ARCHIVE.value, // queue/inbox/archive
        override var queuePosition: Int? = null,
        override var isFavorite: Boolean = false,
        override var isPlayed: Boolean = false
) : Parcelable, Episode {

    //@get:com.google.firebase.firestore.Exclude
    override var localStatus: Int = 0
    //@get:com.google.firebase.firestore.ServerTimestamp
    override var timeCreated: Date? = null
    //@get:com.google.firebase.firestore.ServerTimestamp
    override var timeUpdate: Long? = null
    //@get:com.google.firebase.firestore.ServerTimestamp
    override var timePlayed: Long? = null

    override fun publishedFormat(): String =
            SimpleDateFormat("d/M/yyyy[' ']['T'][H:mm[:ss[.S]]][X]").let {
                return it.format(releaseDate)
            }

    override fun durationFormat(): String? =
            duration?.let {
                Instant.ofEpochSecond(it.toLong()).let {
                    it.atZone(ZoneOffset.UTC).toLocalDateTime().let {
                        return String.format("%sm%s", it.minute.toString(), it.second.toString())
                    }
                }
            }
}