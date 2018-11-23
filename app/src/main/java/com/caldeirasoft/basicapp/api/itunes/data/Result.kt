package com.caldeirasoft.basicapp.api.itunes.data

import com.google.gson.annotations.SerializedName

/**
 * Created by Edmond on 12/02/2018.
 */
class Result {
    @SerializedName("artistName")
    var artistName:String = ""
    @SerializedName("trackName")
    var trackName:String = ""
    @SerializedName("feedUrl")
    var feedUrl:String = ""
    @SerializedName("trackId")
    var trackId:Int = 0
    @SerializedName("artworkUrl100")
    var artwork:String = ""
}