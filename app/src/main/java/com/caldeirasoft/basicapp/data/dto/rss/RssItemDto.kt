package com.caldeirasoft.basicapp.data.dto.rss

import org.jonnyzzz.kotlin.xml.bind.XAttribute
import org.jonnyzzz.kotlin.xml.bind.XText
import org.jonnyzzz.kotlin.xml.bind.jdom.JXML

/**
 * Created by Edmond on 12/02/2018.
 */
class RssItemDto {
    var title by JXML / "title" / XText
    var link by JXML / "link" / XText
    var summary by JXML / "itunes:summary" / XText
    var duration by JXML / "itunes:duration" / XText
    var pubDate by JXML / "pubDate" / XText
    var guid by JXML / "guid" / XText
    var description by JXML / "description" / XText
    var mediaUrl by JXML / "enclosure" / XAttribute("url")
    var mediaLength by JXML / "enclosure" / XAttribute("length")
    var mediaType by JXML / "enclosure" / XAttribute("type")
}