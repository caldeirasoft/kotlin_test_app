package com.caldeirasoft.castly.data.dto.itunes

import com.squareup.moshi.Json

/**
 * Created by Edmond on 12/02/2018.
 */
class ResultIdDto {
    @Json(name = "resultIds")
    lateinit var resultIds:List<Long>
}