package com.caldeirasoft.castly.service.playback.const

import com.caldeirasoft.castly.domain.model.MediaID
import com.caldeirasoft.castly.domain.model.SectionState

class Constants {
    companion object {
        val MEDIA_ROOT: String = MediaID(SectionState.ROOT).asString()

        // keys used to indicate extra values
        const val EXTRA_DATE = "android.media.extra.DATE"
        const val EXTRA_DURATION = "android.media.extra.DURATION"
        const val EXTRA_SECTION = "android.media.extra.SECTION"
        const val EXTRA_FAVORITE = "android.media.extra.FAVORITE"

        // metadata custom keys
        const val METADATA_KEY_IN_DATABASE = "android.media.metadata.IN_DATABASE"

        /**
         * Custom property that holds whether an item is [STATUS_FAVORITE] or [STATUS_NOT_FAVORITE].
         */
        const val METADATA_KEY_FAVORITE_STATUS = "android.media.metadata.FAVORITE_STATUS"

        // metadata custome values
        const val STATUS_NOT_FAVORITE = 0;
        const val STATUS_FAVORITE = 1;

        const val STATUS_NOT_IN_DATABASE = 0;
        const val STATUS_IN_DATABASE = 1;

        // podcast commands
        const val COMMAND_CODE_PODCAST_SUBSCRIBE = "command.podcast.subscribe"
        const val COMMAND_CODE_PODCAST_UNSUBSCRIBE = "command.podcast.unsubscribe"
        const val COMMAND_CODE_PODCAST_GET_DESCRIPTION = "command.podcast.get_description"
        const val COMMAND_CODE_PODCAST_REFRESH = "command.podcast.refresh"

        // queue commands
        const val COMMAND_CODE_QUEUE_CLEAR = "command.queue.clear"
        const val COMMAND_CODE_QUEUE_ADD_ITEM = "command.queue.add_item"
        const val COMMAND_CODE_QUEUE_ADD_ITEM_AT = "command.queue.add_item_at"
        const val COMMAND_CODE_QUEUE_REMOVE_ITEM = "command.queue.remove_item"

        // episode commands
        const val COMMAND_CODE_EPISODE_MARK_PLAYED = "command.episodes.markPlayed"
        const val COMMAND_CODE_EPISODE_MARK_UNPLAYED = "command.episodes.markUnplayed"
        const val COMMAND_CODE_EPISODE_MARK_FAVORITE = "command.episodes.markFavorite"
        const val COMMAND_CODE_EPISODE_MARK_UNFAVORITE = "command.episodes.markUnfavorite"
        const val COMMAND_CODE_EPISODE_ARCHIVE = "command.episodes.archive"
    }
}