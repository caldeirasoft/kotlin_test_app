package com.caldeirasoft.basicapp.api.itunes.data

import com.google.gson.annotations.SerializedName

/**
 * Created by Edmond on 12/02/2018.
 */
class Top {
    @SerializedName("feed")
    lateinit var feed:Feed
}