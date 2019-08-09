package com.caldeirasoft.castly.data.dto.itunes

import com.squareup.moshi.Json
import org.threeten.bp.LocalDateTime

/**
 * Created by Edmond on 12/02/2018.
 */
class SearchResultDto {
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
        var trackId:Long = 0

        @Json(name = "artworkUrl100")
        var artworkUrl100:String = ""

        @Json(name = "artworkUrl600")
        var artworkUrl600:String = ""

        @Json(name = "releaseDate")
        var releaseDate: LocalDateTime = LocalDateTime.MIN

        @Json(name = "trackCount")
        var trackCount:Int = 0

        @Json(name = "contentAdvisoryRating")
        var contentAdvisoryRating:String = "" //clean/explicit

    }
}