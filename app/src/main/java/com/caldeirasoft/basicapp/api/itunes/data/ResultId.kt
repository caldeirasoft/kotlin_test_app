package com.caldeirasoft.basicapp.api.itunes.data

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

/**
 * Created by Edmond on 12/02/2018.
 */
class ResultId {
    @Json(name = "resultIds")
    lateinit var resultIds:List<String>
}