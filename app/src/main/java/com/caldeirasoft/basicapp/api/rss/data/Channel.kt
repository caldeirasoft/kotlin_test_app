package com.caldeirasoft.basicapp.api.rss.data

import com.google.gson.annotations.SerializedName
import org.simpleframework.xml.*

/**
 * Created by Edmond on 12/02/2018.
 */
@Root(name = "channel", strict = false)
class Channel {

    @get:Element(name = "title")
    @set:Element(name = "title")
    lateinit var title:String

    @get:Element(name = "language")
    @set:Element(name = "language")
    var language:String? = null

    @get:Element(name = "description")
    @set:Element(name = "description")
    var description:String? = null

    @Namespace(reference = "http://www.itunes.com/dtds/podcast-1.0.dtd", prefix = "itunes")
    @get:Element(name = "summary")
    @set:Element(name = "summary")
    var summary:String? = null

    @Namespace(reference = "http://www.itunes.com/dtds/podcast-1.0.dtd", prefix = "itunes")
    @get:Element(name = "author")
    @set:Element(name = "author")
    var author:String? = null

    @get:Element(name = "image")
    @set:Element(name = "image" )
    @Namespace(reference = "http://www.itunes.com/dtds/podcast-1.0.dtd", prefix = "itunes")
    var image: Image? = null

    @get:Element(name = "link")
    @set:Element(name = "link")
    var link:String? = null

    @get:ElementList(name = "item", required = false, inline = true)
    @set:ElementList(name = "item", required = false, inline = true)
    lateinit var listItems: List<Item>
}