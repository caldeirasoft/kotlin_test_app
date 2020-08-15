package com.caldeirasoft.castly.service.playback.const

import com.caldeirasoft.castly.domain.model.entities.MediaID
import com.caldeirasoft.castly.domain.model.entities.SectionState

class Constants {
    companion object {
        val MEDIAID_ROOT: String = MediaID(SectionState.ROOT).asString()

        // keys used to indicate extra values
        const val EXTRA_DATE = "android.media.extra.DATE"
        const val EXTRA_DURATION = "android.media.extra.DURATION"
        const val EXTRA_SECTION = "android.media.extra.SECTION"
        const val EXTRA_FAVORITE = "android.media.extra.FAVORITE"

        const val EXTRA_PODCAST = "android.media.browse.extra.podcast"
        const val EXTRA_EPISODE = "android.media.browse.extra.episode"

        const val EXTRA_PAGE_SIZE = "android.media.browse.extra.pageSize"
        const val EXTRA_RELOAD_ALL = "android.media.browse.extra.reloadAll"

        // metadata custom keys
        const val METADATA_KEY_ID = "android.media.metadata.ID"
        const val METADATA_KEY_SUBSCRIBED = "android.media.metadata.SUBSCRIBED"
        const val METADATA_KEY_GENRES = "android.media.metadata.GENRES"
        const val METADATA_KEY_OTHER_PODCASTS_BY_ARTIST = "android.media.metadata.OTHER_PODCASTS_BY_ARTIST"
        const val METADATA_KEY_OTHER_PODCASTS_FOLLOWED = "android.media.metadata.OTHER_PODCASTS_FOLLOWED"
        const val METADATA_KEY_WEBSITE_URL = "android.media.metadata.WEBSITE_URL"
        const val METADATA_KEY_COPYRIGHT = "android.media.metadata.COPYRIGHT"
        const val METADATA_KEY_SECTION = "android.media.metadata.SECTION"

        const val METADATA_KEY_FAVORITE_STATUS = "android.media.metadata.FAVORITE_STATUS"
        const val METADATA_KEY_PLAY_STATUS = "android.media.metadata.PLAY_STATUS"
        const val METADATA_KEY_CURRENT_POSITION = "android.media.metadata.CURRENT_POSITION"
        const val METADATA_KEY_TIME_PLAYED = "android.media.metadata.TIME_PLAYED"
        const val METADATA_KEY_PLAYBACK_STATUS = "android.media.metadata.PLAYBACK_STATUS"
        const val METADATA_KEY_TRACK_TYPE = "android.media.metadata.TRACK_TYPE"
        const val METADATA_KEY_QUEUE_POSITION = "android.media.metadata.QUEUE_POSITION"

        // metadata custome values
        const val STATUS_NOT_FAVORITE = 0
        const val STATUS_FAVORITE = 1

        const val STATUS_NOT_SUBSCRIBED = 0
        const val STATUS_SUBSCRIBED = 1

        // podcast commands
        const val COMMAND_CODE_PODCAST_SUBSCRIBE = "command.podcast.subscribe"
        const val COMMAND_CODE_PODCAST_UNSUBSCRIBE = "command.podcast.unsubscribe"
        const val COMMAND_CODE_PODCAST_GET_DESCRIPTION = "command.podcast.get_description"
        const val COMMAND_CODE_PODCAST_REFRESH = "command.podcast.refresh"

        // playback command
        const val COMMAND_PLAYBACK_UPDATE_INFO = "command.playback.update_info"

        // queue commands
        const val COMMAND_CODE_QUEUE_CLEAR = "command.queue.clear"
        const val COMMAND_CODE_QUEUE_ADD_ITEM = "command.queue.add_item"
        const val COMMAND_CODE_QUEUE_ADD_ITEM_AT = "command.queue.add_item_at"
        const val COMMAND_CODE_QUEUE_REMOVE_ITEM = "command.queue.remove_item"

        // episode commands
        const val COMMAND_CODE_EPISODE_TOGGLE_PLAY = "command.episodes.togglePlay"
        const val COMMAND_CODE_EPISODE_TOGGLE_FAVORITE = "command.episodes.toggleFavorite"
        const val COMMAND_CODE_EPISODE_ARCHIVE = "command.episodes.archive"

        // constants
        const val UPDATE_INFO = "UPDATE_INFO"
        const val PROGRESS_UPDATE_EVENT = "PROGRESS_UPDATE_EVENT"
        const val CURRENT_PROGRESS = "CURRENT_PROGRESS"
        const val PLAYLIST_INFO_EVENT = "PLAYLIST_INFO_EVENT"
        const val HAS_NEXT = "HAS_NEXT"
        const val HAS_PREV = "HAS_PREV"

        // The volume we set the media player to when we lose audio focus, but are
        // allowed to reduce the volume instead of stopping playback.
        internal const val VOLUME_DUCK = 0.2f
        // The volume we set the media player when we have audio focus.
        internal const val VOLUME_NORMAL = 1.0f

    }
}