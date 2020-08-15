package com.caldeirasoft.castly.data.dto.itunes

import com.squareup.moshi.Json

/**
 * Created by Edmond on 12/02/2018.
 */
class ContentRatingDto {
    @Json(name = "riaa")
    var riaa: RiaaResult = RiaaResult()

    class RiaaResult {
        @Json(name = "name")
        var name: String = ""

        @Json(name = "rank")
        var rank: Int = 0
    }
}