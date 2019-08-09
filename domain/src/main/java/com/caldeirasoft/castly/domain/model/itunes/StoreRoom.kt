package com.caldeirasoft.castly.domain.model.itunes

import com.caldeirasoft.castly.domain.model.Podcast

/**
 * Created by Edmond on 09/02/2018.
 */
open class StoreRoom(
        id: Long,
        name:String,
        ids: List<Long>) : StoreCollection(id, name) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as StoreRoom
        if (name != other.name) return false
        return true
    }
}