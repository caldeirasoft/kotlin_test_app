package com.caldeirasoft.basicapp.api.itunes.data

import com.google.gson.annotations.SerializedName

/**
 * Created by Edmond on 12/02/2018.
 */
class Id : ImLabel() {
    /**
     *
     * @param arguments
     * The label
     */
    @SerializedName("attributes")
    lateinit var attributes:IdAttributes
}