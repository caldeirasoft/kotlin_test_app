package com.caldeirasoft.basicapp.api.feedly.data

import com.google.gson.annotations.SerializedName

/**
 * Created by Edmond on 12/02/2018.
 */
class Link {
    @SerializedName("href")
    lateinit var href:String

    @SerializedName("type")
    lateinit var type:String

    @SerializedName("length")
    var length:Long = 0
}