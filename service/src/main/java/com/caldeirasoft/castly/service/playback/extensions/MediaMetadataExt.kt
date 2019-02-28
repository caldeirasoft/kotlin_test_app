/*
 * Copyright 2017 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caldeirasoft.castly.service.playback.extensions

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.media.browse.MediaBrowser
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaDescriptionCompat.EXTRA_DOWNLOAD_STATUS
import android.support.v4.media.MediaDescriptionCompat.STATUS_NOT_DOWNLOADED
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DOWNLOAD_STATUS
import com.caldeirasoft.castly.domain.model.*
import com.caldeirasoft.castly.service.playback.const.Constants
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.EXTRA_DATE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.EXTRA_DURATION
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.EXTRA_FAVORITE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.METADATA_KEY_FAVORITE_STATUS
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.METADATA_KEY_IN_DATABASE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.STATUS_FAVORITE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.STATUS_IN_DATABASE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.STATUS_NOT_FAVORITE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.STATUS_NOT_IN_DATABASE
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import java.util.concurrent.TimeUnit

/**
 * Useful extensions for [MediaMetadataCompat].
 */
inline val MediaMetadataCompat.id get() = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)

inline val MediaMetadataCompat.title get() = getString(MediaMetadataCompat.METADATA_KEY_TITLE)

inline val MediaMetadataCompat.artist get() = getString(MediaMetadataCompat.METADATA_KEY_ARTIST)

inline val MediaMetadataCompat.duration get() = getLong(MediaMetadataCompat.METADATA_KEY_DURATION)

inline val MediaMetadataCompat.album get() = getString(MediaMetadataCompat.METADATA_KEY_ALBUM)

inline val MediaMetadataCompat.author get() = getString(MediaMetadataCompat.METADATA_KEY_AUTHOR)

inline val MediaMetadataCompat.writer get() = getString(MediaMetadataCompat.METADATA_KEY_WRITER)

inline val MediaMetadataCompat.composer get() = getString(MediaMetadataCompat.METADATA_KEY_COMPOSER)

inline val MediaMetadataCompat.compilation
    get() = getString(MediaMetadataCompat.METADATA_KEY_COMPILATION)

inline val MediaMetadataCompat.date get() = getString(MediaMetadataCompat.METADATA_KEY_DATE)

inline val MediaMetadataCompat.genre get() = getString(MediaMetadataCompat.METADATA_KEY_GENRE)

inline val MediaMetadataCompat.trackNumber
    get() = getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER)

inline val MediaMetadataCompat.trackCount
    get() = getLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS)

inline val MediaMetadataCompat.discNumber
    get() = getLong(MediaMetadataCompat.METADATA_KEY_DISC_NUMBER)

inline val MediaMetadataCompat.albumArtist
    get() = getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST)

inline val MediaMetadataCompat.art get() = getBitmap(MediaMetadataCompat.METADATA_KEY_ART)

inline val MediaMetadataCompat.artUri
    get() = this.getString(MediaMetadataCompat.METADATA_KEY_ART_URI).tryParseUri()

inline val MediaMetadataCompat.albumArt
    get() = getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART)

inline val MediaMetadataCompat.albumArtUri
    get() = this.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI).tryParseUri()

inline val MediaMetadataCompat.displayTitle
    get() = getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE)

inline val MediaMetadataCompat.displaySubtitle
    get() = getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE)

inline val MediaMetadataCompat.displayDescription
    get() = getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION)

inline val MediaMetadataCompat.displayIcon
    get() = getBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON)

inline val MediaMetadataCompat.displayIconUri
    get() = this.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI).tryParseUri()

inline val MediaMetadataCompat.mediaUri
    get() = this.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI).tryParseUri()

inline val MediaMetadataCompat.downloadStatus
    @SuppressLint("WrongConstant")
    get() = getLong(METADATA_KEY_DOWNLOAD_STATUS)

/**
* Custom property for storing whether a [MediaMetadataCompat] item represents an
* item that is [MediaItem.FLAG_BROWSABLE] or [MediaItem.FLAG_PLAYABLE].
*/
@MediaBrowserCompat.MediaItem.Flags
inline val MediaMetadataCompat.flag
    @SuppressLint("WrongConstant")
    get() = this.getLong(METADATA_KEY_UAMP_FLAGS).toInt()

@MediaBrowserCompat.MediaItem.Flags
inline val MediaMetadataCompat.favoriteStatus
    @SuppressLint("WrongConstant")
    get() = this.getLong(METADATA_KEY_FAVORITE_STATUS).toInt()

@MediaBrowserCompat.MediaItem.Flags
inline val MediaMetadataCompat.inDatabaseStatus
    @SuppressLint("WrongConstant")
    get() = this.getLong(METADATA_KEY_IN_DATABASE).toInt()

/**
 * Useful extensions for [MediaMetadataCompat.Builder].
 */

// These do not have getters, so create a message for the error.
const val NO_GET = "Property does not have a 'get'"

inline var MediaMetadataCompat.Builder.id: String
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, value)
    }

inline var MediaMetadataCompat.Builder.title: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_TITLE, value)
    }

inline var MediaMetadataCompat.Builder.artist: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_ARTIST, value)
    }

inline var MediaMetadataCompat.Builder.album: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_ALBUM, value)
    }

inline var MediaMetadataCompat.Builder.date: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_DATE, value)
    }

inline var MediaMetadataCompat.Builder.duration: Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putLong(MediaMetadataCompat.METADATA_KEY_DURATION, value)
    }

inline var MediaMetadataCompat.Builder.genre: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_GENRE, value)
    }

inline var MediaMetadataCompat.Builder.mediaUri: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, value)
    }

inline var MediaMetadataCompat.Builder.albumArtUri: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, value)
    }

inline var MediaMetadataCompat.Builder.albumArt: Bitmap?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, value)
    }

inline var MediaMetadataCompat.Builder.trackNumber: Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, value)
    }

inline var MediaMetadataCompat.Builder.trackCount: Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, value)
    }

inline var MediaMetadataCompat.Builder.displayTitle: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, value)
    }

inline var MediaMetadataCompat.Builder.displaySubtitle: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, value)
    }

inline var MediaMetadataCompat.Builder.displayDescription: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, value)
    }

inline var MediaMetadataCompat.Builder.displayIconUri: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, value)
    }

inline var MediaMetadataCompat.Builder.downloadStatus: Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putLong(MediaMetadataCompat.METADATA_KEY_DOWNLOAD_STATUS, value)
    }

/**
 * Custom property for storing whether a [MediaMetadataCompat] item represents an
 * item that is [MediaItem.FLAG_BROWSABLE] or [MediaItem.FLAG_PLAYABLE].
 */
@MediaBrowserCompat.MediaItem.Flags
inline var MediaMetadataCompat.Builder.flag: Int
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    @SuppressLint("WrongConstant")
    set(value) {
        putLong(METADATA_KEY_UAMP_FLAGS, value.toLong())
    }

/**
 * Custom property for storing whether a [MediaMetadataCompat] item represents an
 * item that is [STATUS_FAVORITE] or [STATUS_NOT_FAVORITE].
 */
@MediaBrowserCompat.MediaItem.Flags
inline var MediaMetadataCompat.Builder.favoriteStatus: Int
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    @SuppressLint("WrongConstant")
    set(value) {
        putLong(METADATA_KEY_FAVORITE_STATUS, value.toLong())
    }

/**
 * Custom property for storing whether a [MediaMetadataCompat] item represents an
 * item that is [STATUS_NOT_IN_DATABASE] or [STATUS_IN_DATABASE].
 */
@MediaBrowserCompat.MediaItem.Flags
inline var MediaMetadataCompat.Builder.inDatabaseStatus: Int
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    @SuppressLint("WrongConstant")
    set(value) {
        putLong(METADATA_KEY_IN_DATABASE, value.toLong())
    }

/**
 * Custom property for retrieving a [MediaDescriptionCompat] which also includes
 * all of the keys from the [MediaMetadataCompat] object in its extras.
 *
 * These keys are used by the ExoPlayer MediaSession extension when announcing metadata changes.
 */
inline val MediaMetadataCompat.fullDescription
    get() =
        MediaDescriptionCompat.Builder().also {
            it.id = id
            it.title = title
            it.subtitle = displaySubtitle
            it.description = displayDescription
            it.iconUri = albumArtUri
            it.extras = bundle
            it.mediaUri = mediaUri
        }.build()

/**
 * Extension method for building an [ExtractorMediaSource] from a [MediaMetadataCompat] object.
 *
 * For convenience, place the [MediaDescriptionCompat] into the tag so it can be retrieved later.
 */
fun MediaMetadataCompat.toMediaSource(dataSourceFactory: DataSource.Factory) =
        ExtractorMediaSource.Factory(dataSourceFactory)
                .setTag(fullDescription)
                .createMediaSource(mediaUri)

/**
 * Extension method for building a [ConcatenatingMediaSource] given a [List]
 * of [MediaMetadataCompat] objects.
 */
fun List<MediaMetadataCompat>.toMediaSource(
        dataSourceFactory: DataSource.Factory
): ConcatenatingMediaSource {

    val concatenatingMediaSource = ConcatenatingMediaSource()
    forEach {
        concatenatingMediaSource.addMediaSource(it.toMediaSource(dataSourceFactory))
    }
    return concatenatingMediaSource
}


/**
 * Extension method for building a [MediaMetadataCompat] from a [Episode] object
 */
inline val Episode.mediaDescription: MediaDescriptionCompat
    @SuppressLint("WrongConstant")
    get() = MediaMetadataCompat.Builder().also {
            // The duration from the JSON is given in seconds, but the rest of the code works in
            // milliseconds. Here's where we convert to the proper units.
            val durationMs = TimeUnit.SECONDS.toMillis(duration ?: 0)

            it.id = episodeId
            it.title = title
            it.displayTitle = title
            it.album = podcastTitle
            it.displaySubtitle = podcastTitle
            it.displayIconUri = imageUrl
            it.albumArtUri = imageUrl
            it.displayDescription = description
            it.mediaUri = mediaUrl
            it.date = publishedFormat()
            it.duration = durationMs
            it.downloadStatus = STATUS_NOT_DOWNLOADED
            it.favoriteStatus = if(isFavorite) STATUS_FAVORITE else STATUS_NOT_FAVORITE
        }.build().fullDescription


/**
 * Extension method for building a [MediaDescriptionCompat] from a [Podcast] object
 */
inline val Podcast.mediaDescription: MediaDescriptionCompat
    get() = asMediaDescription(false)

/**
 * Extension method for building a [MediaDescriptionCompat] from a [Podcast] object
 */
inline val Podcast.mediaDescriptionInDb: MediaDescriptionCompat
    get() = asMediaDescription(true)


/**
 * Extension method for building a [MediaDescriptionCompat] from a [Podcast] object
 */
fun Podcast.asMediaDescription(inDb: Boolean = false): MediaDescriptionCompat {
    return MediaMetadataCompat.Builder().also {
        it.id = MediaID(SectionState.PODCAST, feedUrl).asString()
        it.title = title
        it.artist = authors
        it.displayTitle = title
        it.displayDescription = description
        it.albumArtUri = imageUrl
        it.displayIconUri = imageUrl
        it.date = (updated ?: 0).toString()
        it.inDatabaseStatus = if (inDb) STATUS_IN_DATABASE else STATUS_NOT_IN_DATABASE
    }.build().fullDescription
}

/**
 * Extension method for building a [MediaItem] from a [Podcast] object
 */
fun Podcast.asMediaItem(inDb: Boolean = false): MediaBrowserCompat.MediaItem {
    return MediaBrowserCompat.MediaItem(this.asMediaDescription(inDb), FLAG_BROWSABLE)
}


/**
 * Extension method for building a [Podcast] from a [MediaItem] object
 */
fun MediaBrowserCompat.MediaItem.toPodcast(): Podcast {
    return PodcastEntity().also { podcast ->
        this.description.also {
            podcast.feedUrl = MediaID().fromString(it.mediaId.orEmpty()).id
            podcast.title = it.title?.toString().orEmpty()
            podcast.authors = it.subtitle?.toString().orEmpty()
            podcast.description = it.description?.toString().orEmpty()
            podcast.imageUrl = it.iconUri?.toString().orEmpty()
            it.extras?.let {
                podcast.updated = it.getLong(EXTRA_DATE)
            }
        }
    }
}

/**
 * Custom property that holds whether an item is [MediaItem.FLAG_BROWSABLE] or
 * [MediaItem.FLAG_PLAYABLE].
 */
const val METADATA_KEY_UAMP_FLAGS = "com.example.android.uamp.media.METADATA_KEY_UAMP_FLAGS"

