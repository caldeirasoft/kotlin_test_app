package com.caldeirasoft.basicapp.api.itunes.data

import com.google.gson.annotations.SerializedName

/**
 * Created by Edmond on 12/02/2018.
 */
open class ImLabel {
    /**
     *
     * @param label
     * The label
     */
    @SerializedName("label")
    lateinit var label:String
}