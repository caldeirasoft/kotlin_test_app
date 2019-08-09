package com.caldeirasoft.castly.domain.model.itunes

/**
 * Created by Edmond on 09/02/2018.
 */
data class StoreData(
        val trending: List<PodcastArtwork> = arrayListOf(),
        val groups: List<StoreGroup> = arrayListOf(),
        val name:String = ""
) {
}