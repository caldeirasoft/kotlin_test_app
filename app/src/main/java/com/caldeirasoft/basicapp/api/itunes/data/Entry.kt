package com.caldeirasoft.basicapp.api.itunes.data

import com.google.gson.annotations.SerializedName

/**
 * Created by Edmond on 12/02/2018.
 */
class Entry {
    @SerializedName("im:name")
    lateinit var imName:ImLabel

    @SerializedName("im:image")
    var imImage:List<ImImage> = ArrayList()

    @SerializedName("summary")
    lateinit var summary:ImLabel

    @SerializedName("title")
    lateinit var title:ImLabel

    @SerializedName("link")
    lateinit var link:Link

    @SerializedName("id")
    lateinit var id:Id

    @SerializedName("im:artist")
    lateinit var imArtist:ImLabel
}