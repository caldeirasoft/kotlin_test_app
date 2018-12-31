package com.caldeirasoft.basicapp.data.repository

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.adapters.Converters
import androidx.room.*
import com.caldeirasoft.basicapp.data.db.DbTypeConverter
import com.caldeirasoft.basicapp.data.entity.Podcast
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by Edmond on 09/02/2018.
 */
@Parcelize
class PodcastArtwork : Parcelable {
    var podcast: Podcast? = null
    var artworkUrl: String = ""
    var bgColor: Int = 0
    var textColor: Int = 0
}