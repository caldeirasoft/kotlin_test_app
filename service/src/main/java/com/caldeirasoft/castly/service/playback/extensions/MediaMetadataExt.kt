package com.caldeirasoft.castly.service.playback.extensions

import android.graphics.Bitmap
import android.net.Uri
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.common.MediaMetadata.*
import com.caldeirasoft.castly.domain.model.entities.Episode
import com.caldeirasoft.castly.domain.model.entities.MediaID
import com.caldeirasoft.castly.domain.model.entities.Podcast
import com.caldeirasoft.castly.domain.model.entities.SectionState
import java.util.concurrent.TimeUnit

/**
 * Useful extensions for [MediaMetadataCompat].
 */
inline val MediaMetadata.id get() = getString(MediaMetadata.METADATA_KEY_MEDIA_ID)

inline val MediaMetadata.title get() = getString(MediaMetadata.METADATA_KEY_TITLE)

inline val MediaMetadata.artist get() = getString(MediaMetadata.METADATA_KEY_ARTIST)

inline val MediaMetadata.duration get() = getLong(MediaMetadata.METADATA_KEY_DURATION)

inline val MediaMetadata.album get() = getString(MediaMetadata.METADATA_KEY_ALBUM)

inline val MediaMetadata.author get() = getString(MediaMetadata.METADATA_KEY_AUTHOR)

inline val MediaMetadata.writer get() = getString(MediaMetadata.METADATA_KEY_WRITER)

inline val MediaMetadata.composer get() = getString(MediaMetadata.METADATA_KEY_COMPOSER)

inline val MediaMetadata.compilation
    get() = getString(MediaMetadata.METADATA_KEY_COMPILATION)

inline val MediaMetadata.date get() = getString(MediaMetadata.METADATA_KEY_DATE)

inline val MediaMetadata.genre get() = getString(MediaMetadata.METADATA_KEY_GENRE)

inline val MediaMetadata.trackNumber
    get() = getLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER)

inline val MediaMetadata.trackCount
    get() = getLong(MediaMetadata.METADATA_KEY_NUM_TRACKS)

inline val MediaMetadata.discNumber
    get() = getLong(MediaMetadata.METADATA_KEY_DISC_NUMBER)

inline val MediaMetadata.albumArtist
    get() = getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)

inline val MediaMetadata.art get() = getBitmap(MediaMetadata.METADATA_KEY_ART)

inline val MediaMetadata.artUri
    get() = Uri.parse(this.getString(MediaMetadata.METADATA_KEY_ART_URI))

inline val MediaMetadata.albumArt
    get() = getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)

inline val MediaMetadata.albumArtUri
    get() = Uri.parse(this.getString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI))

inline val MediaMetadata.displayTitle
    get() = getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)

inline val MediaMetadata.displaySubtitle
    get() = getString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE)

inline val MediaMetadata.displayDescription
    get() = getString(MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION)

inline val MediaMetadata.displayIcon
    get() = getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON)

inline val MediaMetadata.displayIconUri
    get() = Uri.parse(this.getString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI))

inline val MediaMetadata.mediaUri
    get() = Uri.parse(this.getString(MediaMetadata.METADATA_KEY_MEDIA_URI))

inline val MediaMetadata.downloadStatus
    get() = getLong(MediaMetadata.METADATA_KEY_DOWNLOAD_STATUS)

inline val MediaMetadata.browsable
    get() = this.getLong(MediaMetadata.METADATA_KEY_BROWSABLE).toInt()

inline val MediaMetadata.playable
    get() = this.getLong(MediaMetadata.METADATA_KEY_PLAYABLE).toInt()

/**
 * Useful extensions for [MediaMetadata.Builder].
 */

// These do not have getters, so create a message for the error.
const val NO_GET = "Property does not have a 'get'"

inline var MediaMetadata.Builder.id: String
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        putString(MediaMetadata.METADATA_KEY_MEDIA_ID, value.toString())
    }

inline var MediaMetadata.Builder.title: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        putString(MediaMetadata.METADATA_KEY_TITLE, value)
    }

inline var MediaMetadata.Builder.artist: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        putString(MediaMetadata.METADATA_KEY_ARTIST, value)
    }

inline var MediaMetadata.Builder.album: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        putString(MediaMetadata.METADATA_KEY_ALBUM, value)
    }

inline var MediaMetadata.Builder.date: String
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        putString(MediaMetadata.METADATA_KEY_DATE, value)
    }

inline var MediaMetadata.Builder.duration: Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        putLong(MediaMetadata.METADATA_KEY_DURATION, value)
    }

inline var MediaMetadata.Builder.genre: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        putString(MediaMetadata.METADATA_KEY_GENRE, value)
    }

inline var MediaMetadata.Builder.mediaUri: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        putString(MediaMetadata.METADATA_KEY_MEDIA_URI, value)
    }

inline var MediaMetadata.Builder.albumArtUri: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, value)
    }

inline var MediaMetadata.Builder.albumArt: Bitmap?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, value)
    }

inline var MediaMetadata.Builder.trackNumber: Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        putLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER, value)
    }

inline var MediaMetadata.Builder.trackCount: Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        putLong(MediaMetadata.METADATA_KEY_NUM_TRACKS, value)
    }

inline var MediaMetadata.Builder.displayTitle: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, value)
    }

inline var MediaMetadata.Builder.displaySubtitle: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, value)
    }

inline var MediaMetadata.Builder.displayDescription: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        putString(MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION, value)
    }

inline var MediaMetadata.Builder.displayIconUri: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI, value)
    }

inline var MediaMetadata.Builder.downloadStatus: Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        putLong(MediaMetadata.METADATA_KEY_DOWNLOAD_STATUS, value)
    }

inline var MediaMetadata.Builder.browsable: Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        putLong(MediaMetadata.METADATA_KEY_BROWSABLE, value)
    }

inline var MediaMetadata.Builder.playable: Int
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadata.Builder")
    set(value) {
        putLong(MediaMetadata.METADATA_KEY_PLAYABLE, value.toLong())
    }


/**
 * Extension method for building a [MediaMetadataCompat] from a [Episode] object
 */
inline val Episode.mediaMetadata: MediaMetadata
    get() = MediaMetadata.Builder().also {
        // The duration from the JSON is given in seconds, but the rest of the code works in
        // milliseconds. Here's where we convert to the proper units.
        val durationMs = TimeUnit.SECONDS.toMillis(duration?.toLong() ?: 0L)

        it.id = id.toString()
        it.album = podcastName
        it.artist = podcastName
        it.title = name
        it.date = publishedFormat()
        it.duration = durationMs
        it.mediaUri = mediaUrl
        it.albumArtUri = getArtwork(100)

        // To make things easier for *displaying* these, set the display properties as well.
        it.displayTitle = name
        it.displaySubtitle = podcastName
        it.displayDescription = podcastName
        it.displayIconUri = getArtwork(100)

        // Add downloadStatus to force the creation of an "extras" bundle in the resulting
        // MediaMetadataCompat object. This is needed to send accurate metadata to the
        // media session during updates.
        it.downloadStatus = STATUS_NOT_DOWNLOADED

        it.browsable = BROWSABLE_TYPE_NONE
        it.playable = 1
    }.build()


/**
 * Extension method for building a [MediaDescriptionCompat] from a [Podcast] object
 */
inline val Podcast.mediaMetadata: MediaMetadata
    get() = MediaMetadata.Builder().also {
        it.id = MediaID(SectionState.PODCAST, id).asString()
        it.title = name
        it.artist = artistName
        it.displayDescription = description
        it.albumArtUri = getArtwork(100)

        it.browsable = BROWSABLE_TYPE_ALBUMS
        it.playable = 0
        /*it.setExtras(Bundle().apply {
            if (inDb) {
                putBoolean(METADATA_KEY_IN_DATABASE, true)
            }
        })*/
    }.build()


/**
 * Extension method for building a [MediaItem] from a [Podcast] object
 */
fun Podcast.toMediaItem(): MediaItem {
    return MediaItem.Builder().setMetadata(this.mediaMetadata).build()
}


/**
 * Custom property that holds whether an item is [MediaItem.FLAG_BROWSABLE] or
 * [MediaItem.FLAG_PLAYABLE].
 */
const val METADATA_KEY_UAMP_FLAGS = "com.example.android.uamp.media.METADATA_KEY_UAMP_FLAGS"