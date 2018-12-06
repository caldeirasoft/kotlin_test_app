package com.caldeirasoft.basicapp.api.feedly.data

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

/**
 * Created by Edmond on 12/02/2018.
 */
class StreamEntries {
    @Json(name = "id")
    lateinit var id:String

    @Json(name = "title")
    lateinit var title:String

    @Json(name = "updated")
    var updated:Long = 0

    @Json(name = "continuation")
    var continuation:String? = null

    @Json(name = "items")
    var items:List<Entry> = ArrayList()
}