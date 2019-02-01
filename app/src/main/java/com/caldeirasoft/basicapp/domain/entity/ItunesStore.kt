package com.caldeirasoft.basicapp.domain.entity

/**
 * Created by Edmond on 09/02/2018.
 */
data class ItunesStore(
        var trending: List<PodcastArtwork> = arrayListOf(),
        var sections: List<ItunesSection> = arrayListOf(),
        var isLoading: Boolean = true
) {
    companion object {
        fun default() = ItunesStore()
    }
}