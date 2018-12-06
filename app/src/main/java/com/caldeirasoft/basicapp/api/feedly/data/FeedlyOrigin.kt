package com.caldeirasoft.basicapp.api.feedly.data

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

/**
 * Created by Edmond on 12/02/2018.
 */
class FeedlyOrigin {
    @Json(name = "streamId")
    lateinit var streamId:String

    @Json(name = "title")
    lateinit var title:String

    @Json(name = "htmlUrl")
    lateinit var htmlUrl:String
}