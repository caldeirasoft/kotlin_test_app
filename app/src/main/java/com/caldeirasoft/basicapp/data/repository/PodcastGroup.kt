package com.caldeirasoft.basicapp.data.repository

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.caldeirasoft.basicapp.data.db.DbTypeConverter
import com.caldeirasoft.basicapp.data.entity.Podcast
import java.util.*

/**
 * Created by Edmond on 09/02/2018.
 */
class PodcastGroup() {
    var name:String = ""
    var ids: List<Int> = ArrayList()
    var podcasts = MutableLiveData<List<Podcast>>()
}