package com.caldeirasoft.castly.domain.model

import android.os.Parcelable
import androidx.room.*
import com.caldeirasoft.castly.data.datasources.local.database.DbTypeConverter
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by Edmond on 09/02/2018.
 */
@Entity(tableName = "podcasts")
@TypeConverters(DbTypeConverter::class)
@Parcelize
class PodcastEntity : Parcelable, Podcast {
    @PrimaryKey override var feedUrl: String = ""
    override var title: String = ""
    override var trackId: Int? = null
    override var description: String? = null
    override var authors: String? = null
    override var updated: Long? = null
    override var link: String? = null
    override var imageUrl: String? = null
    override var bigImageUrl: String? = null
    override var subscribeAction: Int = 0 // queue,inbox,addfirst,addlast
    override var isInSync: Boolean = false

    override val feedId: String
            get() = "feed/" + feedUrl

    @Ignore
    override var isInDatabase: Boolean = false

    //@get:com.google.firebase.firestore.Exclude
    override var localStatus:Int = 0

    //@get:com.google.firebase.firestore.ServerTimestamp
    override var timeCreated: Date? = null
    //@get:com.google.firebase.firestore.ServerTimestamp
    override var timeUpdate: Date? = null
}