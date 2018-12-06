package com.caldeirasoft.basicapp.api.feedly.data

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

/**
 * Created by Edmond on 12/02/2018.
 */
class StreamEntryIds {
    @Json(name = "continuation")
    var continuation:String? = null

    @Json(name = "items")
    var ids:List<String> = ArrayList()
}