package com.caldeirasoft.basicapp.api.rss.retrofit

import com.caldeirasoft.basicapp.api.rss.data.Feed
import com.caldeirasoft.basicapp.api.rss.data.RssChannel
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import org.jdom2.input.SAXBuilder
import org.jonnyzzz.kotlin.xml.bind.jdom.JDOM
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url
import java.net.URL

/**
 * Created by Edmond on 12/02/2018.
 */
interface RssAPI {
    @GET
    fun getItems(@Url url:String): Call<String>;
}