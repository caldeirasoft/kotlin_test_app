package com.caldeirasoft.castly.data.dto.feedly

import com.squareup.moshi.Json

/**
 * Created by Edmond on 12/02/2018.
 */
class EntryDto {
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
    var summary: FeedlyText? = null

    @Json(name = "content")
    var content: FeedlyText? = null

    @Json(name = "origin")
    lateinit var origin: FeedlyOrigin

    @Json(name = "enclosure")
    var enclosure:List<Link> = ArrayList()

    /**
     * Link
     */
    class Link {
        @Json(name = "href")
        lateinit var href:String

        @Json(name = "type")
        lateinit var type:String

        @Json(name = "length")
        var length:Long = 0
    }

    /**
     * FeedlyOrigin
     */
    class FeedlyOrigin {
        @Json(name = "streamId")
        lateinit var streamId:String

        @Json(name = "title")
        lateinit var title:String

        @Json(name = "htmlUrl")
        lateinit var htmlUrl:String
    }

    /**
     * Feedly text
     */
    class FeedlyText {
        @Json(name = "content")
        lateinit var content:String
    }
}