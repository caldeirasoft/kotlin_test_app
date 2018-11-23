package com.caldeirasoft.basicapp.api.feedly.data

import com.google.gson.annotations.SerializedName

/**
 * Created by Edmond on 12/02/2018.
 */
class FeedlyOrigin {
    @SerializedName("streamId")
    lateinit var streamId:String

    @SerializedName("title")
    lateinit var title:String

    @SerializedName("htmlUrl")
    lateinit var htmlUrl:String
}