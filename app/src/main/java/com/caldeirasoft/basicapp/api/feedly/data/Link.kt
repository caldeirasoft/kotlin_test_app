package com.caldeirasoft.basicapp.api.feedly.data

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

/**
 * Created by Edmond on 12/02/2018.
 */
class Link {
    @Json(name = "href")
    lateinit var href:String

    @Json(name = "type")
    lateinit var type:String

    @Json(name = "length")
    var length:Long = 0
}