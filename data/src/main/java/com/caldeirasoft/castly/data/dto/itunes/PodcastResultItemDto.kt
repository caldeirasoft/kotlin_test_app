package com.caldeirasoft.castly.data.dto.itunes

import com.caldeirasoft.castly.data.features.serializers.LocalDateSerializer
import com.caldeirasoft.castly.data.features.serializers.LocalDateTimeSerializer
import com.squareup.moshi.Json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

/**
 * Created by Edmond on 12/02/2018.
 */
class PodcastResultItemDto {
    @Json(name = "name")
    var name: String = ""

    @Json(name = "artistName")
    var artistName: String = ""

    @Json(name = "url")
    var url: String = ""

    @Json(name = "feedUrl")
    var feedUrl: String = ""

    @Json(name = "id")
    var id: Long = 0

    @Json(name = "artistId")
    var artistId: Long = 0

    @Json(name = "trackCount")
    var trackCount: Int = 0

    @Json(name = "releaseDate")
    @Serializable(with = LocalDateSerializer::class)
    var releaseDate: LocalDate = LocalDate.MIN

    @Json(name = "releaseDateTime")
    @Serializable(with = LocalDateTimeSerializer::class)
    var releaseDateTime: LocalDateTime = LocalDateTime.MIN

    @Json(name = "copyright")
    var copyright: String = ""

    @Json(name = "artwork")
    lateinit var artwork: ArtworkDto

    @Json(name = "userRating")
    lateinit var userRating: UserRatingDto

    @Json(name = "description")
    lateinit var description: Description

    @Json(name = "contentRatingsBySystem")
    lateinit var contentRatingsBySystem: ContentRatingDto

    @Json(name = "genres")
    var genres: List<GenreDto> = emptyList()

    @Json(name = "childrenIds")
    var childrenIds: List<Long> = emptyList()

    @Json(name = "children")
    var children: Map<Long, ProductDvResultChildrenItem> = emptyMap()

    class Description {
        @Json(name = "standard")
        var standard: String = ""

        @Json(name = "short")
        var short: String = ""
    }

    class ProductDvResultChildrenItem {
        @Json(name = "name")
        var name: String = ""

        @Json(name = "artistName")
        var artistName: String = ""

        @Json(name = "collectionName")
        var collectionName: String = ""

        @Json(name = "url")
        var url: String = ""

        @Json(name = "feedUrl")
        var feedUrl: String = ""

        @Json(name = "id")
        var id: Long = 0

        @Json(name = "artistId")
        var artistId: Long = 0

        @Json(name = "releaseDate")
        var releaseDate: LocalDate = LocalDate.MIN

        @Json(name = "releaseDateTime")
        var releaseDateTime: LocalDateTime = LocalDateTime.MIN

        @Json(name = "podcastEpisodeGuid")
        var podcastEpisodeGuid: String = ""

        @Json(name = "podcastEpisodeType")
        var podcastEpisodeType: String = ""

        @Json(name = "podcastEpisodeSeason")
        var podcastEpisodeSeason: Int? = null

        @Json(name = "podcastEpisodeNumber")
        var podcastEpisodeNumber: Int? = null

        @Json(name = "podcastEpisodeWebsiteUrl")
        var podcastEpisodeWebsiteUrl: String? = null

        @Json(name = "genres")
        var genres: List<GenreDto> = arrayListOf()

        @Json(name = "contentRatingsBySystem")
        lateinit var contentRatingsBySystem: ContentRatingDto

        @Json(name = "description")
        lateinit var description: Description

        @Json(name = "offers")
        var offers: List<ProductDvResultChildrenItemOffers> = arrayListOf()

        class ProductDvResultChildrenItemOffers {
            @Json(name = "download")
            lateinit var download: ProductDvResultChildrenItemDownload

            @Json(name = "assets")
            var assets: List<ProductDvResultChildrenItemAsset> = arrayListOf()
        }

        class ProductDvResultChildrenItemDownload {
            @Json(name = "url")
            var url: String = ""

            @Json(name = "type")
            var type: String = ""
        }

        class ProductDvResultChildrenItemAsset {
            @Json(name = "flavor") //standardAudio/sdVideo/sd480pVideo/720pHdVideo/1080pHdVideo
            var flavor: String = ""

            @Json(name = "fileExtension")
            var extension: String = ""

            @Json(name = "duration")
            var duration: Int? = null
        }
    }
}