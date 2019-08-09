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
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DOWNLOAD_STATUS
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.METADATA_KEY_CURRENT_POSITION
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.METADATA_KEY_FAVORITE_STATUS
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.METADATA_KEY_PLAYBACK_STATUS
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.METADATA_KEY_SECTION
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.METADATA_KEY_TIME_PLAYED

/**
 * Useful extensions for [MediaDescriptionCompat].
 */
inline var MediaDescriptionCompat.id
    get() = extras?.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
    set(value) {
        extras?.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, value)
    }

inline var MediaDescriptionCompat.artist
    get() = extras?.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
    set(value) {
        extras?.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, value)
    }

inline var MediaDescriptionCompat.duration
    get() = extras?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION) ?: 0
    set(value) {
        extras?.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, value)
    }

inline var MediaDescriptionCompat.album
    get() = extras?.getString(MediaMetadataCompat.METADATA_KEY_ALBUM)
    set(value) {
        extras?.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, value)
    }

inline var MediaDescriptionCompat.author
    get() = extras?.getString(MediaMetadataCompat.METADATA_KEY_AUTHOR)
    set(value) {
        extras?.putString(MediaMetadataCompat.METADATA_KEY_AUTHOR, value)
    }

inline var MediaDescriptionCompat.writer
    get() = extras?.getString(MediaMetadataCompat.METADATA_KEY_WRITER)
    set(value) {
        extras?.putString(MediaMetadataCompat.METADATA_KEY_WRITER, value)
    }

inline var MediaDescriptionCompat.composer
    get() = extras?.getString(MediaMetadataCompat.METADATA_KEY_COMPOSER)
    set(value) {
        extras?.putString(MediaMetadataCompat.METADATA_KEY_COMPOSER, value)
    }

inline var MediaDescriptionCompat.compilation
    get() = extras?.getString(MediaMetadataCompat.METADATA_KEY_COMPILATION)
    set(value) {
        extras?.putString(MediaMetadataCompat.METADATA_KEY_COMPILATION, value)
    }

inline var MediaDescriptionCompat.date
    get() = extras?.getString(MediaMetadataCompat.METADATA_KEY_DATE)
    set(value) {
        extras?.putString(MediaMetadataCompat.METADATA_KEY_DATE, value)
    }

inline var MediaDescriptionCompat.genre
    get() = extras?.getString(MediaMetadataCompat.METADATA_KEY_GENRE)
    set(value) {
        extras?.putString(MediaMetadataCompat.METADATA_KEY_GENRE, value)
    }

inline var MediaDescriptionCompat.trackNumber
    get() = extras?.getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER) ?: 0
    set(value) {
        extras?.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, value)
    }

inline var MediaDescriptionCompat.trackCount
    get() = extras?.getLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS) ?: 0
    set(value) {
        extras?.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, value)
    }

inline var MediaDescriptionCompat.discNumber
    get() = extras?.getLong(MediaMetadataCompat.METADATA_KEY_DISC_NUMBER) ?: 0
    set(value) {
        extras?.putLong(MediaMetadataCompat.METADATA_KEY_DISC_NUMBER, value)
    }

inline var MediaDescriptionCompat.albumArtist
    get() = extras?.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST)
    set(value) {
        extras?.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, value)
    }

inline var MediaDescriptionCompat.artUri
    get() = extras?.getString(MediaMetadataCompat.METADATA_KEY_ART_URI).tryParseUri()
    set(value) {
        extras?.putString(MediaMetadataCompat.METADATA_KEY_ART_URI, value.toString())
    }

inline var MediaDescriptionCompat.albumArtUri
    get() = extras?.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI).tryParseUri()
    set(value) {
        extras?.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, value.toString())
    }


inline var MediaDescriptionCompat.displayTitle
    get() = extras?.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE)
    set(value) {
        extras?.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, value)
    }

inline var MediaDescriptionCompat.displaySubtitle
    get() = extras?.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE)
    set(value) {
        extras?.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, value)
    }

inline var MediaDescriptionCompat.displayDescription
    get() = extras?.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION)
    set(value) {
        extras?.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, value)
    }

inline var MediaDescriptionCompat.displayIconUri
    get() = extras?.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI).tryParseUri()
    set(value) {
        extras?.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, value.toString())
    }

inline var MediaDescriptionCompat.mediaUri2
    get() = extras?.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI).tryParseUri()
    set(value) {
        extras?.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, value.toString())
    }

inline var MediaDescriptionCompat.downloadStatus
    @SuppressLint("WrongConstant")
    get() = extras?.getLong(METADATA_KEY_DOWNLOAD_STATUS) ?: 0
    set(value) {
        extras?.putLong(MediaMetadataCompat.METADATA_KEY_DOWNLOAD_STATUS, value)
    }

@MediaBrowserCompat.MediaItem.Flags
inline var MediaDescriptionCompat.favoriteStatus
    @SuppressLint("WrongConstant")
    get() = extras?.getLong(METADATA_KEY_FAVORITE_STATUS)?.toInt() ?: 0
    set(value) {
        extras?.putLong(METADATA_KEY_FAVORITE_STATUS, value.toLong())
    }

@MediaBrowserCompat.MediaItem.Flags
inline var MediaDescriptionCompat.section
    @SuppressLint("WrongConstant")
    get() = extras?.getLong(METADATA_KEY_SECTION)?.toInt() ?: 0
    set(value) {
        extras?.putLong(METADATA_KEY_SECTION, value.toLong())
    }

@MediaBrowserCompat.MediaItem.Flags
inline var MediaDescriptionCompat.currentPosition
    @SuppressLint("WrongConstant")
    get() = extras?.getLong(METADATA_KEY_CURRENT_POSITION) ?: 0
    set(value) {
        extras?.putLong(METADATA_KEY_CURRENT_POSITION, value)
    }

@MediaBrowserCompat.MediaItem.Flags
inline var MediaDescriptionCompat.timePlayed
    @SuppressLint("WrongConstant")
    get() = extras?.getLong(METADATA_KEY_TIME_PLAYED) ?: 0
    set(value) {
        extras?.putLong(METADATA_KEY_TIME_PLAYED, value)
    }

@MediaBrowserCompat.MediaItem.Flags
inline var MediaDescriptionCompat.playbackStatus
    @SuppressLint("WrongConstant")
    get() = extras?.getInt(METADATA_KEY_PLAYBACK_STATUS) ?: STATE_NONE
    set(value) {
        extras?.putInt(METADATA_KEY_PLAYBACK_STATUS, value)
    }

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

