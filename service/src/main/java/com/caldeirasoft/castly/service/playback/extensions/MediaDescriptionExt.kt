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
import android.support.v4.media.MediaDescriptionCompat.STATUS_NOT_DOWNLOADED
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DOWNLOAD_STATUS
import com.caldeirasoft.castly.domain.model.*
import com.caldeirasoft.castly.service.playback.const.Constants
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import java.util.concurrent.TimeUnit

/**
 * Useful extensions for [MediaDescriptionCompat.Builder].
 */

inline var MediaDescriptionCompat.Builder.id: String
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaDescriptionCompat.Builder")
    set(value) {
        setMediaId(value)
    }

inline var MediaDescriptionCompat.Builder.title: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaDescriptionCompat.Builder")
    set(value) {
        setTitle(value)
    }

inline var MediaDescriptionCompat.Builder.subtitle: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaDescriptionCompat.Builder")
    set(value) {
        setSubtitle(value)
    }

inline var MediaDescriptionCompat.Builder.description: String?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaDescriptionCompat.Builder")
    set(value) {
        setDescription(value)
    }

inline var MediaDescriptionCompat.Builder.iconUri: Uri?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaDescriptionCompat.Builder")
    set(value) {
        setIconUri(value)
    }

inline var MediaDescriptionCompat.Builder.mediaUri: Uri?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaDescriptionCompat.Builder")
    set(value) {
        setMediaUri(value)
    }

inline var MediaDescriptionCompat.Builder.extras: Bundle?
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaDescriptionCompat.Builder")
    set(value) {
        setExtras(value)
    }

/**
 * Custom property for retrieving a [MediaDescriptionCompat] which also includes
 * all of the keys from the [MediaMetadataCompat] object in its extras.
 *
 * These keys are used by the ExoPlayer MediaSession extension when announcing metadata changes.
 */
inline val MediaDescriptionCompat.metadata
    get() =
        MediaMetadataCompat.Builder().also {
            it.id = this.mediaId.orEmpty()
            this.extras?.apply {
                it.title = getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                it.displayTitle = getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                it.artist = getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
                it.album = getString(MediaMetadataCompat.METADATA_KEY_ALBUM)
                it.duration = getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
                it.artist = getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
                it.date = getString(MediaMetadataCompat.METADATA_KEY_DATE)
                it.albumArtUri = getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)
                it.displayDescription = getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION)
                it.mediaUri = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI)
                it.downloadStatus = getLong(MediaMetadataCompat.METADATA_KEY_DOWNLOAD_STATUS)
                it.favoriteStatus = getLong(Constants.METADATA_KEY_FAVORITE_STATUS).toInt()
                it.inDatabaseStatus = getLong(Constants.METADATA_KEY_IN_DATABASE).toInt()
            }
        }.build()
