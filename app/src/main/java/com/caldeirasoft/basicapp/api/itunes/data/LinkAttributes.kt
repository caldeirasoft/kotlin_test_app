package com.caldeirasoft.basicapp.api.itunes.data

import com.google.gson.annotations.SerializedName

/**
 * Created by Edmond on 12/02/2018.
 */
class LinkAttributes {
    @SerializedName("rel")
    lateinit var rel:String

    @SerializedName("type")
    lateinit var type:String

    @SerializedName("href")
    lateinit var href:String
}