package com.caldeirasoft.castly.data.dto.feedly

import com.squareup.moshi.Json

/**
 * Created by Edmond on 12/02/2018.
 */
class StreamEntryIdsDto {
    @Json(name = "continuation")
    var continuation:String? = null

    @Json(name = "items")
    var ids:List<String> = ArrayList()
}