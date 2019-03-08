package com.caldeirasoft.castly.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.caldeirasoft.castly.data.datasources.local.database.DbTypeConverter
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Edmond on 09/02/2018.
 */
@Entity(tableName = "episodes"
        , indices = arrayOf(Index(value = ["feedUrl", "mediaUrl"], unique = true))
/*        ,foreignKeys = arrayOf(ForeignKey(entity = Podcast::class,
                                         parentColumns = arrayOf("feedId"),
                                         childColumns = arrayOf("feedId"),
                                         onDelete = CASCADE))*/
)
@TypeConverters(DbTypeConverter::class)
@Parcelize
data class EpisodeEntity @JvmOverloads constructor(
        @PrimaryKey override var episodeId: String,
        override var feedUrl: String = "",
        override var title: String = "",
        override var published: Long
) : Parcelable, Episode
{
    override var description: String? = null
    override var duration: Long? = null
    override var playbackPosition: Long? = null
    override var imageUrl: String? = null
    override var bigImageUrl: String? = null
    override var podcastTitle: String = ""
    override var guid: String? = null
    override var link: String? = null

    override var mediaUrl: String = ""
    override var mediaType: String? = null
    override var mediaLength: Long? = null

    override var section: Int = SectionState.ARCHIVE.value // queue/inbox/archive
    override var queuePosition: Int? = null
    override var isFavorite: Boolean = false

    //@get:com.google.firebase.firestore.Exclude
    override var localStatus:Int = 0
    //@get:com.google.firebase.firestore.ServerTimestamp
    override var timeCreated: Date? = null
    //@get:com.google.firebase.firestore.ServerTimestamp
    override var timeUpdate: Long? = null
    //@get:com.google.firebase.firestore.ServerTimestamp
    override var timePlayed: Long? = null

    override fun publishedFormat():String =
            SimpleDateFormat("d/M/yyyy[' ']['T'][H:mm[:ss[.S]]][X]").let {
                val epoch = Date(published)
                return it.format(epoch)
            }

    override fun durationFormat(): String? =
            duration?.let {
                Instant.ofEpochMilli(it).let {
                    it.atZone(ZoneOffset.UTC).toLocalDateTime().let {
                        return String.format("%sm%s", it.minute.toString(), it.second.toString())
                    }
                }
            }
}