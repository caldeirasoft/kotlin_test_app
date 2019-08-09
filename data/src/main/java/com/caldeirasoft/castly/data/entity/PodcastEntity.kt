package com.caldeirasoft.castly.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.caldeirasoft.castly.data.datasources.local.converters.DbTypeConverter
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDateTime
import java.util.*

/**
 * Created by Edmond on 09/02/2018.
 */
@Entity(tableName = "podcasts")
@Parcelize
class PodcastEntity(@PrimaryKey override var id: Long) : Parcelable, Podcast {
    override var name: String = ""
    override var artistName: String = ""
    override var description: String? = null

    override var feedUrl: String = ""
    override var releaseDate: LocalDateTime = LocalDateTime.MIN
    override var podcastWebsiteUrl: String = ""
    override var copyright: String = ""
    override var contentAdvisoryRating: String = "" //🅴
    override var trackCount: Int = 0

    override var artwork: String = ""
    override var artworkHeight: Int? = null
    override var artworkWidth: Int? = null

    override var isSubscribed: Boolean = false
    override var subscribeAction: Int = 0 // queue,inbox,addfirst,addlast

    //@get:com.google.firebase.firestore.Exclude
    override var localStatus:Int = 0

    override var timeCreated: Date? = null
    override var timeUpdate: Date? = null

    @Ignore
    @IgnoredOnParcel
    override var episodes: List<Episode> = emptyList()

    override var genres: List<Genre> = emptyList()

    @Ignore
    override var podcastsByArtist: List<Long> = emptyList()

    @Ignore
    override var podcastsListenersAlsoFollow: List<Long> = emptyList()
}