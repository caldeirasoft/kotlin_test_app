package com.caldeirasoft.basicapp.data.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*


/**
 * Created by Edmond on 09/02/2018.
 */
class SectionWithCount() {
    var QueueCount: Int = 0
    var InboxCount: Int = 0
    var FavoritesCount: Int = 0
    var HistoryCount: Int = 0
}