package com.caldeirasoft.castly.data.dto.itunes

import com.caldeirasoft.castly.data.features.serializers.LocalDateSerializer
import com.caldeirasoft.castly.data.features.serializers.LocalDateTimeSerializer
import com.squareup.moshi.Json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.util.ArrayList

/**
 * Created by Edmond on 12/02/2018.
 */
class LookupItemDto {
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
    var artistId: Long? = null

    @Json(name = "collectionId")
    var collectionId: Long? = null

    @Json(name = "kind")
    var kind: String = ""

    @Json(name = "releaseDate")
    @Serializable(with = LocalDateSerializer::class)
    var releaseDate: LocalDate? = null

    @Json(name = "releaseDateTime")
    @Serializable(with = LocalDateTimeSerializer::class)
    var releaseDateTime: LocalDateTime? = null

    @Json(name = "artwork")
    var artwork: ArtworkDto? = null

    @Json(name = "editorialArtwork")
    var editorialArtwork: EditorialArtwork? = null

    @Json(name = "userRating")
    var userRating: UserRatingDto? = null

    @Json(name = "contentRatingsBySystem")
    var contentRatingsBySystem: ContentRatingDto? = null

    @Json(name = "podcastType")
    var podcastType: String? = null

    @Json(name = "podcastEpisodeType")
    var podcastEpisodeType: String? = null

    @Json(name = "trackCount")
    var trackCount: Int? = null

    @Json(name = "collectionName")
    var collectionName: String? = null

    @Json(name = "collection")
    lateinit var collection: Map<Long, LookupItemDto>

    @Json(name = "genres")
    var genres: List<GenreDto> = arrayListOf()

    @Json(name = "offers")
    val offers: List<Offers> = arrayListOf()

    @Json(name = "uber")
    var uber: UberResult? = null

    class UberResult {
        @Json(name = "description")
        var description: String? = null

        @Json(name = "masterArt")
        var masterArt: List<ArtworkDto> = ArrayList()
    }

    class EditorialArtwork {
        @Json(name = "storeFlowcase")
        var storeFlowcase: ArtworkDto? = null
    }

    class Offers {
        @Json(name = "type")
        var type: String = ""

        @Json(name = "download")
        var download: Download? = null

        @Json(name = "assets")
        val assets: List<Asset> = arrayListOf()

        class Download {
            @Json(name = "url")
            var url: String = ""

            @Json(name = "type")
            var type: String = ""
        }

        class Asset {
            @Json(name = "flavor")
            var flavor: String = ""

            @Json(name = "fileExtension")
            var fileExtension: String = ""

            @Json(name = "duration")
            var duration: Int = 0
        }
    }
}