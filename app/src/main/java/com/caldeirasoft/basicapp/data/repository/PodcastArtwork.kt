package com.caldeirasoft.basicapp.data.repository

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.adapters.Converters
import androidx.room.*
import com.caldeirasoft.basicapp.data.db.DbTypeConverter
import com.caldeirasoft.basicapp.data.entity.Podcast
import java.util.*

/**
 * Created by Edmond on 09/02/2018.
 */
class PodcastArtwork() : Parcelable {
    var podcast: Podcast? = null
    var artworkUrl: String = ""
    var bgColor: Int = 0
    var textColor: Int = 0

    constructor(parcel: Parcel) : this() {
        artworkUrl = parcel.readString()
        bgColor = parcel.readInt()
        textColor = parcel.readInt()
        podcast = parcel.readValue(Podcast::class.java.classLoader) as? Podcast
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(artworkUrl)
        parcel.writeInt(bgColor)
        parcel.writeInt(textColor)
        parcel.writeValue(podcast)
    }

    override fun describeContents(): Int {
        return 0
    }

    @JvmField val CREATOR = object  : Parcelable.Creator<PodcastArtwork> {
        override fun createFromParcel(parcel: Parcel): PodcastArtwork {
            return PodcastArtwork(parcel)
        }

        override fun newArray(size: Int): Array<PodcastArtwork?> {
            return arrayOfNulls(size)
        }
    }
}