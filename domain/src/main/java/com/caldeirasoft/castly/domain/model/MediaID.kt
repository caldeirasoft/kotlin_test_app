package com.caldeirasoft.castly.domain.model

import android.provider.MediaStore

class MediaID(var type: SectionState = SectionState.ROOT, var id: String = "NA") {

    companion object {
        private const val TYPE = "type: "
        private const val MEDIA_ID = "media_id: "
        private const val SEPARATOR = " | "

        fun fromString(s: String?): MediaID =
                MediaID().apply {
                    s?.let {
                        this.type = SectionState.valueOf(s.substring(6, s.indexOf(SEPARATOR)))
                        this.id = s.substring(s.indexOf(SEPARATOR) + 3 + 10, s.lastIndexOf(SEPARATOR))
                    }
                    return this
                }
    }

    fun asString(): String {
        return TYPE + type + SEPARATOR + MEDIA_ID + id + SEPARATOR
    }

    fun fromString(s: String): MediaID {
        this.type = SectionState.valueOf(s.substring(6, s.indexOf(SEPARATOR)))
        this.id = s.substring(s.indexOf(SEPARATOR) + 3 + 10, s.lastIndexOf(SEPARATOR))
        return this
    }
}