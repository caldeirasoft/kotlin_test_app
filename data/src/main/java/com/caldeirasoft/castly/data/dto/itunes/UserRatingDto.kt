package com.caldeirasoft.castly.data.dto.itunes

import com.squareup.moshi.Json

/**
 * Created by Edmond on 12/02/2018.
 */
class UserRatingDto {
    @Json(name = "value")
    var value: Float = 0F

    @Json(name = "ratingCount")
    var ratingCount: Int = 0

    @Json(name = "ratingCountList")
    var ratingCountList: List<Int> = arrayListOf()

    /**
     * Calculate user rating
     */
    fun getRating(): Float = value
}