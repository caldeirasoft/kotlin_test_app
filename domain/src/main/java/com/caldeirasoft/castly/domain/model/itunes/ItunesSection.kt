package com.caldeirasoft.castly.domain.model.itunes

import com.caldeirasoft.castly.domain.model.Podcast

/**
 * Created by Edmond on 09/02/2018.
 */
class ItunesSection(var name:String,
                    var ids: List<Int>,
                    val podcasts: List<Podcast>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ItunesSection
        if (!name.equals(other.name)) return false
        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    companion object {
        val PAGE_SIZE = 15
    }
}