package com.caldeirasoft.basicapp.api.feedly.data

import com.google.gson.annotations.SerializedName

/**
 * Created by Edmond on 12/02/2018.
 */
class Feed {
    @SerializedName("id")
    lateinit var id:String

    @SerializedName("feedId")
    lateinit var feedId:String

    @SerializedName("title")
    lateinit var name:String

    @SerializedName("visualUrl")
    var image:String? = null

    @SerializedName("converColor")
    var coverColor:String? = null

    @SerializedName("coverUrl")
    var coverUrl:String? = null

    @SerializedName("iconUrl")
    var iconImage:String? = null

    @SerializedName("updated")
    var updated:Long = 0

    @SerializedName("language")
    var language:String? = null

    @SerializedName("description")
    var description:String? = null

    @SerializedName("subscribers")
    var subscribers:Int? = null

    @SerializedName("velocity")
    var velocity:Double? = null

    @SerializedName("state")
    var state:String? = null
}