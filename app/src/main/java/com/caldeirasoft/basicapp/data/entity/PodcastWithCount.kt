package com.caldeirasoft.basicapp.data.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*


/**
 * Created by Edmond on 09/02/2018.
 */
class PodcastWithCount() {
    var feedUrl: String = ""
    var title: String = ""
    var imageUrl: String? = null
    var episodeCount: Int = 0

    fun isPodcast():Boolean = feedUrl.startsWith("feed/")

    companion object {
        val FEED_ID_ALL = "pls/all"
    }
}