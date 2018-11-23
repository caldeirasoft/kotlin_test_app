package com.caldeirasoft.basicapp.api.rss.data

import com.google.gson.annotations.SerializedName
import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Root

/**
 * Created by Edmond on 12/02/2018.
 */
@Root(name = "rss", strict = false)
@Namespace(reference = "http://www.itunes.com/dtds/podcast-1.0.dtd")
class Feed {

    @get:Element(name = "channel")
    @set:Element(name = "channel")
    var channel:Channel? = null
}