package com.caldeirasoft.basicapp.api.feedly.data

import com.google.gson.annotations.SerializedName

/**
 * Created by Edmond on 12/02/2018.
 */
class Entry {
    @SerializedName("id")
    lateinit var id:String

    @SerializedName("fingerprint")
    var fingerPrint:String? = null

    @SerializedName("originId")
    var originId:String? = null

    @SerializedName("title")
    lateinit var title:String

    @SerializedName("author")
    lateinit var author:String

    @SerializedName("published")
    var published:Long? = null

    @SerializedName("crawled")
    var crawled:Long? = null

    @SerializedName("updated")
    var updated:Long? = null

    @SerializedName("summary")
    var summary:FeedlyText? = null

    @SerializedName("content")
    var content:FeedlyText? = null

    @SerializedName("origin")
    lateinit var origin:FeedlyOrigin

    @SerializedName("enclosure")
    var enclosure:List<Link> = ArrayList()
}