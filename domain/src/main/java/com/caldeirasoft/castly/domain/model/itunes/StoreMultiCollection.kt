package com.caldeirasoft.castly.domain.model.itunes

/**
 * Created by Edmond on 09/02/2018.
 */
open class StoreMultiCollection(name:String ) : StoreGroup(name) {

    var multiCollection: MutableList<StoreCollection> = arrayListOf()
    var artworkUrl: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as StoreMultiCollection
        if (name != other.name) return false
        return true
    }
}