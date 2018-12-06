package com.caldeirasoft.basicapp.api.itunes.data

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

/**
 * Created by Edmond on 12/02/2018.
 */
class SearchResult {
    @Json(name = "results")
    var results:List<Result> = ArrayList()

    class Result {
        @Json(name = "artistName")
        var artistName:String = ""

        @Json(name = "trackName")
        var trackName:String = ""

        @Json(name = "feedUrl")
        var feedUrl:String = ""

        @Json(name = "trackId")
        var trackId:Int = 0

        @Json(name = "artworkUrl100")
        var artworkUrl100:String = ""

        @Json(name = "artworkUrl600")
        var artworkUrl600:String = ""
    }
}