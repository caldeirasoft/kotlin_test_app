package com.caldeirasoft.castly.data.dto.itunes

import com.squareup.moshi.Json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by Edmond on 12/02/2018.
 */
class ResultIdDto {
    @SerialName("resultIds")
    lateinit var resultIds:List<Long>
}