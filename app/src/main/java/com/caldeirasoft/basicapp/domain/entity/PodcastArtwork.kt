package com.caldeirasoft.basicapp.domain.entity

import android.os.Parcelable
import com.caldeirasoft.basicapp.domain.entity.Podcast
import kotlinx.android.parcel.Parcelize

/**
 * Created by Edmond on 09/02/2018.
 */
@Parcelize
class PodcastArtwork(var podcast: Podcast) : Parcelable {
    var artworkUrl: String = ""
    var bgColor: Int = 0
    var textColor: Int = 0
}