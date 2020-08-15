package com.caldeirasoft.castly.data.features.serializers

import com.caldeirasoft.castly.data.entity.EpisodeEntity
import com.caldeirasoft.castly.data.entity.PodcastEntity
import com.caldeirasoft.castly.data.models.itunes.GroupingPageDataEntity
import com.caldeirasoft.castly.domain.model.entities.Episode
import com.caldeirasoft.castly.domain.model.entities.Podcast
import com.caldeirasoft.castly.domain.model.itunes.GroupingPageData
import com.caldeirasoft.castly.domain.model.itunes.PodcastCollection
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.SerializersModule

object JsonSerializerProvider {
    @kotlinx.serialization.UnstableDefault
    fun provide(): Json {
        val serializableContentModule = SerializersModule {
            polymorphic(GroupingPageData::class) {
                GroupingPageDataEntity::class with GroupingPageDataEntity.serializer()
                GroupingPageDataEntity.TrendingCollectionEntity::class with GroupingPageDataEntity.TrendingCollectionEntity.serializer()
            }

            polymorphic(GroupingPageData.Collection::class) {
                GroupingPageDataEntity.TrendingCollectionEntity::class with GroupingPageDataEntity.TrendingCollectionEntity.serializer()
                PodcastCollection::class with PodcastCollection.serializer()
            }

            polymorphic(GroupingPageData.TrendingItem::class) {
                GroupingPageDataEntity.TrendingRoom::class with GroupingPageDataEntity.TrendingRoom.serializer()
                GroupingPageDataEntity.TrendingPodcast::class with GroupingPageDataEntity.TrendingPodcast.serializer()
                GroupingPageDataEntity.TrendingProviderArtist::class with GroupingPageDataEntity.TrendingProviderArtist.serializer()
            }

            polymorphic(Podcast::class) {
                PodcastEntity::class with PodcastEntity.serializer()
            }
        }

        return Json(context = serializableContentModule, configuration = JsonConfiguration(ignoreUnknownKeys = true))
    }
}