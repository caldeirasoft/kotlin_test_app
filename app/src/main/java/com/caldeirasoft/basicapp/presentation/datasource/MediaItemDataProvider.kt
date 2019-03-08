package com.caldeirasoft.basicapp.presentation.datasource

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.service.playback.const.Constants
import com.caldeirasoft.castly.service.playback.extensions.*

/**
 * Created by Edmond on 15/02/2018.
 */
data class MediaItemDataProvider(
        var retrievedMediaItems: MutableList<MediaBrowserCompat.MediaItem> = mutableListOf(),
        var continuation: String? = null,
        var isFull: Boolean = false)
{
    // Media items
    fun addItems(mediaItems: List<MediaBrowserCompat.MediaItem>, continuat: String?) {
        retrievedMediaItems.addAll(mediaItems)
        continuation = continuat
        isFull = continuat.isNullOrEmpty()
    }

    /**
     * Get episodes info from DB
     */
    fun updateMediaItemsWithDb(episodes: List<Episode>) {
        // get episode in db
        retrievedMediaItems.forEach { item ->
            episodes.firstOrNull { ep -> ep.episodeId == item.mediaId.toString() }
                    ?.let { episode ->
                        item.description.section = episode.section
                        item.description.trackNumber = episode.queuePosition?.toLong() ?: -1
                        item.description.favoriteStatus =
                                if (episode.isFavorite) Constants.STATUS_FAVORITE
                                else Constants.STATUS_NOT_FAVORITE
                        item.description.duration = episode.duration ?: 0
                        item.description.currentPosition = episode.playbackPosition ?: 0
                        item.description.timePlayed = episode.timePlayed ?: 0
                    }
        }
    }
}