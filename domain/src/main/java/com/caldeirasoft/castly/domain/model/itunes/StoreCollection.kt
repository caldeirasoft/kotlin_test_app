package com.caldeirasoft.castly.domain.model.itunes

import com.caldeirasoft.castly.domain.model.Podcast

/**
 * Created by Edmond on 09/02/2018.
 */
open class StoreCollection(
        val id: Long,
        name:String,
        var type: StoreCollectionType = StoreCollectionType.COLLECTION) : StoreGroup(name) {

    var ids: List<Long> = arrayListOf()
    var podcasts: List<Podcast> = arrayListOf()
    var artworkUrl: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as StoreCollection
        if (name != other.name) return false
        return true
    }
}
