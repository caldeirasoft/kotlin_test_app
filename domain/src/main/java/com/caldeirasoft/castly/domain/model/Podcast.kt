package com.caldeirasoft.castly.domain.model

import android.os.Parcelable
import java.util.*

/**
 * Created by Edmond on 09/02/2018.
 */
interface Podcast : Parcelable {
    var feedUrl: String
    var title: String
    var trackId: Int?
    var description: String?
    var authors: String?
    var updated: Long?
    var link: String?
    var imageUrl: String?
    var bigImageUrl: String?
    var subscribeAction: Int // queue,inbox,addfirst,addlast
    var isInSync: Boolean

    val feedId: String
    var isInDatabase: Boolean

    //@get:com.google.firebase.firestore.Exclude
    var localStatus:Int

    //@get:com.google.firebase.firestore.ServerTimestamp
    var timeCreated: Date?
    //@get:com.google.firebase.firestore.ServerTimestamp
    var timeUpdate: Date?

    companion object {
        val PODCAST_STATE = "podcast_state"
        val PODCAST_FILE_PATH = "podcast_downloaded_file_path"
        val PODCAST_DOWNLOAD_REFERENCE = "podcast_download_reference"
        val PODCAST_PROGRAM_ID = "podcast_program_id"
        val PODCAST_BIG_IMAGE_URL = "podcast_big_image_url"
        val PODCAST_DURATION = "podcast_duration"
        val PODCAST_DATE = "podcast_date"
    }
}