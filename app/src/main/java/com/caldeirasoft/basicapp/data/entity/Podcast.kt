package com.caldeirasoft.basicapp.data.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.caldeirasoft.basicapp.data.db.DbTypeConverter
import kotlinx.android.parcel.Parcelize
import java.net.URLEncoder
import java.util.*

/**
 * Created by Edmond on 09/02/2018.
 */
@Entity(tableName = "podcasts")
@TypeConverters(DbTypeConverter::class)
@Parcelize
class Podcast : Parcelable {
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
    companion object {
        val PODCAST_STATE = "podcast_state"
        val PODCAST_FILE_PATH = "podcast_downloaded_file_path"
        val PODCAST_DOWNLOAD_REFERENCE = "podcast_download_reference"
        val PODCAST_PROGRAM_ID = "podcast_program_id"
        val PODCAST_BIG_IMAGE_URL = "podcast_big_image_url"
        val PODCAST_DURATION = "podcast_duration"
        val PODCAST_DATE = "podcast_date"

        val DEFAULT_PODCAST : Podcast = Podcast()
    }
}