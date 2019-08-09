package com.caldeirasoft.castly.data.dto.itunes

import com.squareup.moshi.Json
import java.util.*

/**
 * Created by Edmond on 12/02/2018.
 */
class ArtworkDto {
    @Json(name = "url")
    var url:String = ""

    @Json(name = "width")
    var width:Int = 0

    @Json(name = "height")
    var height:Int = 0
}