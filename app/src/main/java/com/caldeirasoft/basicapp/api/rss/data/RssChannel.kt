package com.caldeirasoft.basicapp.api.rss.data

import org.jonnyzzz.kotlin.xml.bind.XAnyElements
import org.jonnyzzz.kotlin.xml.bind.XSub
import org.jonnyzzz.kotlin.xml.bind.XText
import org.jonnyzzz.kotlin.xml.bind.jdom.JXML

/**
 * Created by Edmond on 12/02/2018.
 */
class RssChannel {
    var title by JXML / "title" / XText
    var link by JXML / "link" / XText
    var description by JXML / "description" / XText
    var language by JXML / "language" / XText
    var copyright by JXML / "copyright" / XText
    var lastBuildDate by JXML / "lastBuildDate" / XText
    var managinEditor by JXML / "managinEditor" / XText

    var itunesAuthor by JXML / "itunes:author" / XText
    var itunesSubtitle by JXML / "itunes:subtitle" / XText
    var itunesSummary by JXML / "itunes:summary" / XText
    var itunesCategory by JXML / "itunes:category" / XText
    var itunesOwnerName by JXML / "itunes:owner" / "itunes:name" / XText
    var itunesOwnerEmail by JXML / "itunes:owner" / "itunes:email" / XText
    var itunesBlock by JXML / "itunes:block" / XText
    var itunesExplicit by JXML / "itunes:explicit" / XText

    var imageUrl by JXML / "image" / "url" / XText
    var imageTitle by JXML / "image" / "title" / XText
    var imageLink by JXML / "image" / "link" / XText
    var imageWidth by JXML / "image" / "width" / XText
    var imageHeight by JXML / "image" / "height" / XText
    var items by JXML / "item" / XAnyElements / XSub(RssItem::class.java)
}