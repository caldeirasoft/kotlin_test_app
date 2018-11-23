package com.caldeirasoft.basicapp.api.rss.data

import com.google.gson.annotations.SerializedName
import org.simpleframework.xml.*
import java.sql.Time
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Edmond on 12/02/2018.
 */
@Root(name = "item", strict = false)
@NamespaceList(value = [
    Namespace(),
    Namespace(reference = "http://www.itunes.com/dtds/podcast-1.0.dtd", prefix = "itunes"),
    Namespace(reference = "http://search.yahoo.com/mrss/", prefix = "media")
])
class Item {
    @get:Element(name = "title", required = true)
    @set:Element(name = "title", required = true)
    lateinit var title:String

    @get:Element(name = "subtitle")
    @set:Element(name = "subtitle")
    var subtitle:String? = null

    @get:Element(name = "pubDate", required = true)
    @set:Element(name = "pubDate", required = true)
    var publishedDate:String? = null

    @get:Element(name = "duration", required = false)
    @set:Element(name = "duration", required = false)
    lateinit var duration:String

    @get:Element(name = "description", required = true)
    @set:Element(name = "description", required = true)
    var description:String? = null

    @get:Element(name = "summary", required = false)
    @set:Element(name = "summary", required = false)
    var summary:String? = null

    @get:Element(name = "guid", required = false)
    @set:Element(name = "guid", required = false)
    var guid:String? = null

    @Path("enclosure")
    @set:Attribute(name = "url" )
    @get:Attribute(name = "url" )
    var mediaUrl: String? = null

    @Path("enclosure")
    @get:Attribute(name = "length" )
    @set:Attribute(name = "length" )
    var mediaLength: Int = 0

    @Path("enclosure")
    @get:Attribute(name = "type" )
    @set:Attribute(name = "type" )
    lateinit var mediaType: String

    @Path(value = "image")
    @Namespace(reference = "http://www.itunes.com/dtds/podcast-1.0.dtd", prefix = "itunes")
    @get:Attribute(name = "href" )
    @set:Attribute(name = "href" )
    var imageUrl: String? = null

    @get:Element(name = "author", required = false)
    @set:Element(name = "author", required = false)
    var author:String? = null

    val publishedFormat: Date
        get() = itunesDateFormat.parse(publishedDate)

    val durationFormat: Long
        get() {
            val utc = TimeZone.getTimeZone("UTC")
            for (format in itunesDurationFormats) {
                try {
                    format.timeZone = utc
                    return format.parse(duration).time
                } catch (ignored: ParseException) {
                }
            }
            return duration.toLong()
        }
        //= SimpleDateFormat("HH:mm:ss").parse(duration).time

    companion object {
        val itunesDateFormat: DateFormat =  SimpleDateFormat("EEE, dd MMMM yyyy HH:mm:ss zzz", Locale.ENGLISH)

        val itunesDurationFormats: Array<DateFormat> =
                arrayOf(SimpleDateFormat("HH:mm:ss", Locale.ENGLISH),
                        SimpleDateFormat("H:mm:ss", Locale.ENGLISH),
                        SimpleDateFormat("mm:ss", Locale.ENGLISH),
                        SimpleDateFormat("m:ss", Locale.ENGLISH))
    }
}