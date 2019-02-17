package com.caldeirasoft.castly.domain.model

class MediaID(var type: Int? = null, var mediaId: String? = "NA", var caller: String? = currentCaller) {
    companion object {
        const val CALLER_SELF = "self"
        const val CALLER_OTHER = "other"

        private const val TYPE = "type: "
        private const val MEDIA_ID = "media_id: "
        private const val CALLER = "caller: "
        private const val SEPARATOR = " | "

        var currentCaller: String? = MediaID.CALLER_SELF
    }

    //var mediaItem: MediaBrowser.MediaItem? = null

    fun asString(): String {
        return TYPE + type + SEPARATOR + MEDIA_ID + mediaId + SEPARATOR + CALLER + caller
    }

    fun fromString(s: String): MediaID {
        this.type = s.substring(6, s.indexOf(SEPARATOR)).toInt()
        this.mediaId = s.substring(s.indexOf(SEPARATOR) + 3 + 10, s.lastIndexOf(SEPARATOR))
        this.caller = s.substring(s.lastIndexOf(SEPARATOR) + 3 + 8, s.length)
        return this
    }
}