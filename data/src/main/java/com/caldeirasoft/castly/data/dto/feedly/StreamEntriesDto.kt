package com.caldeirasoft.castly.data.dto.feedly

import com.squareup.moshi.Json

/**
 * Created by Edmond on 12/02/2018.
 */
class StreamEntriesDto {
    @Json(name = "id")
    lateinit var id:String

    @Json(name = "title")
    lateinit var title:String

    @Json(name = "updated")
    var updated:Long = 0

    @Json(name = "continuation")
    var continuation:String? = null

    @Json(name = "items")
    var items:List<EntryDto> = ArrayList()
}