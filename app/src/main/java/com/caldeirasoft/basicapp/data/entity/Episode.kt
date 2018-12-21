package com.caldeirasoft.basicapp.data.entity

import android.annotation.SuppressLint
import androidx.room.*
import com.caldeirasoft.basicapp.data.db.DbTypeConverter
import com.caldeirasoft.basicapp.data.enum.SectionState
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
data class Episode @JvmOverloads constructor(
        @PrimaryKey var episodeId: String,
        var feedUrl: String = "",
        var title: String = "",
        var published: Long
)
{
    var description: String? = null
    var duration: Long? = null
    var playbackPosition: Int? = null
    var imageUrl: String? = null
    var bigImageUrl: String? = null
    var podcastTitle: String = ""
    var guid: String? = null
    var link: String? = null

    var mediaUrl: String = ""
    var mediaType: String? = null
    var mediaLength: Long? = null

    var section: Int = SectionState.ARCHIVE.value // queue/inbox/archive
    var queuePosition: Int? = null
    var isFavorite: Boolean = false

    //@get:com.google.firebase.firestore.Exclude
    var localStatus:Int = 0
    //@get:com.google.firebase.firestore.ServerTimestamp
    var timeCreated: Date? = null
    //@get:com.google.firebase.firestore.ServerTimestamp
    var timeUpdate: Date? = null
    //@get:com.google.firebase.firestore.ServerTimestamp
    var timePlayed: Date? = null

    fun publishedFormat():String =
            SimpleDateFormat("dd/MM/yyyy").let {
                val epoch = Date(published)
                return it.format(epoch)
            }

    fun durationFormat(): String? =
            duration?.let {
                Instant.ofEpochMilli(it).let {
                    it.atZone(ZoneOffset.UTC).toLocalDateTime().let {
                        return String.format("%sm%s", it.minute.toString(), it.second.toString())
                    }
                }
            }

    companion object {
        val EPISODE_STATE = "episode_state"
        val EPISODE_FILE_PATH = "episode_downloaded_file_path"
        val EPISODE_DOWNLOAD_REFERENCE = "episode_download_reference"
        val EPISODE_PODCAST_ID = "episode_podcast_id"
        val EPISODE_BIG_IMAGE_URL = "episode_big_image_url"
        val EPISODE_DURATION = "episode_duration"
        val EPISODE_DATE = "episode_date"
    }
}