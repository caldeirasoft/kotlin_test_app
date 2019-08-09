package com.caldeirasoft.castly.data.dto.itunes

import com.squareup.moshi.Json
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

/**
 * Created by Edmond on 12/02/2018.
 */
class LookupItemDto {
    @Json(name = "name")
    var name:String = ""

    @Json(name = "artistName")
    var artistName:String = ""

    @Json(name = "url")
    var url:String = ""

    @Json(name = "feedUrl")
    var feedUrl:String = ""

    @Json(name = "id")
    var id:Long = 0

    @Json(name = "artistId")
    var artistId:Long = 0

    @Json(name = "kind")
    var kind:String = ""

    @Json(name = "releaseDate")
    lateinit var releaseDate:LocalDate

    @Json(name = "releaseDateTime")
    lateinit var releaseDateTime:LocalDateTime

    @Json(name = "artwork")
    lateinit var artwork: ArtworkDto

    @Json(name = "userRating")
    lateinit var userRating: UserRating

    @Json(name = "contentRatingsBySystem")
    lateinit var contentRatingsBySystem: ContentRatingsBySystemResult

    @Json(name = "trackCount")
    var trackCount:Int = 0

    @Json(name = "genres")
    var genres: List<GenreResult> = arrayListOf()

    class UserRating {
        @Json(name = "value")
        var value:String = ""

        @Json(name = "ratingCount")
        var ratingCount:Long = 0

        @Json(name = "ratingCountList")
        var ratingCountList:List<Long> = arrayListOf()
    }

    class GenreResult {
        @Json(name = "genreId")
        var genreId:Long = 0L

        @Json(name = "name")
        var name:String = ""
    }

    class ContentRatingsBySystemResult {
        @Json(name = "riaa")
        lateinit var riaa:RiaaResult

        class RiaaResult {
            @Json(name = "name")
            var name:String = ""

            @Json(name = "rank")
            var rank:Int = 0
        }
    }
}