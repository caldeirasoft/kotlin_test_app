package com.caldeirasoft.basicapp.api.rss.data

import com.google.gson.annotations.SerializedName
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Root

/**
 * Created by Edmond on 12/02/2018.
 */
@Root(name = "image", strict = false)
class Image {

    @get:Attribute(name = "href")
    @set:Attribute(name = "href")
    var href:String? = null
}