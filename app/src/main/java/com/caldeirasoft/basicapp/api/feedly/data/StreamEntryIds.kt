package com.caldeirasoft.basicapp.api.feedly.data

import com.google.gson.annotations.SerializedName

/**
 * Created by Edmond on 12/02/2018.
 */
class StreamEntryIds {
    @SerializedName("continuation")
    var continuation:String? = null

    @SerializedName("items")
    var ids:List<String> = ArrayList()
}