package com.caldeirasoft.basicapp.api.feedly.data

import com.google.gson.annotations.SerializedName

/**
 * Created by Edmond on 12/02/2018.
 */
class StreamEntries {
    @SerializedName("id")
    lateinit var id:String

    @SerializedName("title")
    lateinit var title:String

    @SerializedName("updated")
    var updated:Long = 0

    @SerializedName("continuation")
    var continuation:String? = null

    @SerializedName("items")
    var items:List<Entry> = ArrayList()
}