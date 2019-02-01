package com.caldeirasoft.basicapp.data.dto.feedly

import com.squareup.moshi.Json

/**
 * Created by Edmond on 12/02/2018.
 */
class FeedDto {
    @Json(name = "id")
    lateinit var id:String

    @Json(name = "feedId")
    lateinit var feedId:String

    @Json(name = "title")
    lateinit var name:String

    @Json(name = "visualUrl")
    var image:String? = null

    @Json(name = "converColor")
    var coverColor:String? = null

    @Json(name = "coverUrl")
    var coverUrl:String? = null

    @Json(name = "iconUrl")
    var iconImage:String? = null

    @Json(name = "updated")
    var updated:Long = 0

    @Json(name = "language")
    var language:String? = null

    @Json(name = "description")
    var description:String? = null

    @Json(name = "subscribers")
    var subscribers:Int? = null

    @Json(name = "velocity")
    var velocity:Double? = null

    @Json(name = "state")
    var state:String? = null
}