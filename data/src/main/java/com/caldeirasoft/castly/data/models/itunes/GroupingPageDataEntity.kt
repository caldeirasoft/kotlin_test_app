package com.caldeirasoft.castly.data.models.itunes

import com.caldeirasoft.castly.data.entity.PodcastEntity
import com.caldeirasoft.castly.domain.model.entities.Artwork
import com.caldeirasoft.castly.domain.model.entities.Podcast
import com.caldeirasoft.castly.domain.model.itunes.ArtistItunes
import com.caldeirasoft.castly.domain.model.itunes.GroupingPageData
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Serializable
class GroupingPageDataEntity : GroupingPageData() {
    //val mapPodcastLookup: HashMap<Long, Podcast> = hashMapOf()
    //val mapArtistLookup: HashMap<Long, ArtistItunes> = hashMapOf()
    override val items: ArrayList<Collection> = arrayListOf()

    @Serializable
    @Polymorphic
    class TrendingCollectionEntity(
            override var label: String,
            override var id: Long)
        : TrendingCollection, Collection {
        override var isHeader: Boolean = false
        override val items: ArrayList<TrendingItem> = arrayListOf()
    }

    @Serializable
    @Polymorphic
    class TrendingRoom(override var label: String,
                       override var id: Long,
                       override var url: String,
                       override var artwork: Artwork) : TrendingItem

    @Serializable
    @Polymorphic
    class TrendingPodcast(override var label: String,
                          override var id: Long,
                          override var url: String,
                          override var artwork: Artwork,
                          val podcast: Podcast) : TrendingItem

    @Serializable
    @Polymorphic
    class TrendingProviderArtist(override var label: String,
                                 override var id: Long,
                                 override var url: String,
                                 override var artwork: Artwork,
                                 val artist: ArtistItunes) : TrendingItem
}