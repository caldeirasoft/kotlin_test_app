package com.caldeirasoft.castly.domain.model

import android.os.Parcelable
import java.util.*

interface Episode : Parcelable
{
    var episodeId: String
    var feedUrl: String
    var title: String
    var published: Long

    var description: String?
    var duration: Long?
    var playbackPosition: Long?
    var imageUrl: String?
    var bigImageUrl: String?
    var podcastTitle: String
    var guid: String?
    var link: String?

    var mediaUrl: String
    var mediaType: String?
    var mediaLength: Long?

    var section: Int
    var queuePosition: Int?
    var isFavorite: Boolean

    //@get:com.google.firebase.firestore.Exclude
    var localStatus:Int
    //@get:com.google.firebase.firestore.ServerTimestamp
    var timeCreated: Date?
    //@get:com.google.firebase.firestore.ServerTimestamp
    var timeUpdate: Long?
    //@get:com.google.firebase.firestore.ServerTimestamp
    var timePlayed: Long?

    fun publishedFormat():String

    fun durationFormat(): String?
}