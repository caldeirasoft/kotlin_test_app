package com.caldeirasoft.basicapp.api.feedly.data

import com.squareup.moshi.Json

/**
 * Created by Edmond on 12/02/2018.
 */
class Entry {
    @Json(name = "id")
    lateinit var id:String

    @Json(name = "fingerprint")
    var fingerPrint:String? = null

    @Json(name = "originId")
    var originId:String? = null

    @Json(name = "title")
    lateinit var title:String

    @Json(name = "author")
    lateinit var author:String

    @Json(name = "published")
    var published:Long? = null

    @Json(name = "crawled")
    var crawled:Long? = null

    @Json(name = "updated")
    var updated:Long? = null

    @Json(name = "summary")
    var summary:FeedlyText? = null

    @Json(name = "content")
    var content:FeedlyText? = null

    @Json(name = "origin")
    lateinit var origin:FeedlyOrigin

    @Json(name = "enclosure")
    var enclosure:List<Link> = ArrayList()
}