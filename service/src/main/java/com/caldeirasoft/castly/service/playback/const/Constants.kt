package com.caldeirasoft.castly.service.playback.const

class Constants {
    companion object {
        // metadata custom keys
        const val METADATA_KEY_IN_DATABASE = "android.media.metadata.IN_DATABASE"

        // podcast commands
        const val COMMAND_CODE_PODCAST_SUBSCRIBE = "command.podcast.subscribe"
        const val COMMAND_CODE_PODCAST_UNSUBSCRIBE = "command.podcast.unsubscribe"
        const val COMMAND_CODE_PODCAST_GET_DESCRIPTION = "command.podcast.get_description"
        const val COMMAND_CODE_PODCAST_REFRESH = "command.podcast.refresh"

        // episode commands
        const val COMMAND_CODE_EPISODE_MARK_PLAYED = "command.episodes.markPlayed"
        const val COMMAND_CODE_EPISODE_MARK_UNPLAYED = "command.episodes.markUnplayed"
        const val COMMAND_CODE_EPISODE_MARK_FAVORITE = "command.episodes.markFavorite"
        const val COMMAND_CODE_EPISODE_MARK_UNFAVORITE = "command.episodes.markUnfavorite"
        const val COMMAND_CODE_EPISODE_ARCHIVE = "command.episodes.archive"
    }
}