package com.caldeirasoft.basicapp.data.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.caldeirasoft.basicapp.data.db.DbTypeConverter
import java.net.URLEncoder
import java.util.*

/**
 * Created by Edmond on 09/02/2018.
 */
@Entity(tableName = "podcasts")
@TypeConverters(DbTypeConverter::class)
class Podcast() : Parcelable {
    @PrimaryKey var feedUrl: String = ""
    var title: String = ""
    var trackId: Int? = null
    var description: String? = null
    var authors: String? = null
    var updated: Long? = null
    var link: String? = null
    var imageUrl: String? = null
    var bigImageUrl: String? = null
    var subscribeAction: Int = 0 // queue,inbox,addfirst,addlast
    var isInSync: Boolean = false

    val feedId: String
            get() = "feed/" + feedUrl

    @Ignore
    var isInDatabase: Boolean = false

    //@get:com.google.firebase.firestore.Exclude
    var localStatus:Int = 0

    //@get:com.google.firebase.firestore.ServerTimestamp
    var timeCreated: Date? = null
    //@get:com.google.firebase.firestore.ServerTimestamp
    var timeUpdate: Date? = null

    constructor(parcel: Parcel) : this() {
        feedUrl = parcel.readString()
        title = parcel.readString()
        trackId = parcel.readValue(Int::class.java.classLoader) as? Int
        description = parcel.readString()
        authors = parcel.readString()
        updated = parcel.readValue(Long::class.java.classLoader) as? Long
        imageUrl = parcel.readString()
        bigImageUrl = parcel.readString()
        subscribeAction = parcel.readInt()
        isInSync = parcel.readByte() != 0.toByte()
        isInDatabase = parcel.readByte() != 0.toByte()
        localStatus = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(feedUrl)
        parcel.writeString(title)
        parcel.writeValue(trackId)
        parcel.writeString(description)
        parcel.writeString(authors)
        parcel.writeValue(updated)
        parcel.writeString(imageUrl)
        parcel.writeString(bigImageUrl)
        parcel.writeInt(subscribeAction)
        parcel.writeByte(if (isInSync) 1 else 0)
        parcel.writeByte(if (isInDatabase) 1 else 0)
        parcel.writeInt(localStatus)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val PODCAST_STATE = "podcast_state"
        val PODCAST_FILE_PATH = "podcast_downloaded_file_path"
        val PODCAST_DOWNLOAD_REFERENCE = "podcast_download_reference"
        val PODCAST_PROGRAM_ID = "podcast_program_id"
        val PODCAST_BIG_IMAGE_URL = "podcast_big_image_url"
        val PODCAST_DURATION = "podcast_duration"
        val PODCAST_DATE = "podcast_date"

        @JvmField val CREATOR = object  : Parcelable.Creator<Podcast> {
            override fun createFromParcel(parcel: Parcel): Podcast {
                return Podcast(parcel)
            }

            override fun newArray(size: Int): Array<Podcast?> {
                return arrayOfNulls(size)
            }
        }

        val DEFAULT_PODCAST : Podcast = Podcast()
    }
}