package com.caldeirasoft.castly.data.dto.itunes

import com.squareup.moshi.Json
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.util.*

/**
 * Created by Edmond on 12/02/2018.
 */
class PodcastResultDto {
    @Json(name = "storePlatformData")
    lateinit var storePlatformData: StorePlatformDataResult

    @Json(name = "pageData")
    lateinit var pageData: PageDataResult

    class StorePlatformDataResult {
        @Json(name = "product-dv")
        lateinit var product_dv: ProductDvResult

        class ProductDvResult {
            @Json(name = "results")
            lateinit var results:Map<Long, ProductDvResultItem>

            class ProductDvResultItem {
                @Json(name = "name")
                var name:String = ""

                @Json(name = "artistName")
                var artistName:String = ""

                @Json(name = "url")
                var url:String = ""

                @Json(name = "feedUrl")
                var feedUrl:String = ""

                @Json(name = "id")
                var id:Long = 0

                @Json(name = "artistId")
                var artistId:Long = 0

                @Json(name = "trackCount")
                var trackCount:Int = 0

                @Json(name = "releaseDate")
                lateinit var releaseDate:LocalDateTime

                @Json(name = "releaseDateTime")
                lateinit var releaseDateTime:LocalDateTime

                @Json(name = "artwork")
                lateinit var artwork: Artwork

                @Json(name = "userRating")
                lateinit var userRating: UserRating

                @Json(name = "description")
                lateinit var description: Description

                @Json(name = "contentRatingsBySystem")
                lateinit var contentRatingsBySystem: ContentRatingsBySystemResult

                @Json(name = "genres")
                var genres: List<GenreResult> = arrayListOf()

                @Json(name = "childrenIds")
                var childrenIds: List<Long> = arrayListOf()

                @Json(name = "children")
                var children:Map<Long, ProductDvResultChildrenItem> = emptyMap()

                @Json(name = "copyright")
                var copyright:String = ""

                @Json(name = "podcastEpisodeType")
                var podcastEpisodeType:String = ""

                @Json(name = "podcastEpisodeSeason")
                var podcastEpisodeSeason:Int? = null

                @Json(name = "podcastEpisodeNumber")
                var podcastEpisodeNumber:Int? = null

                @Json(name = "podcastEpisodeWebsiteUrl")
                var podcastEpisodeWebsiteUrl:String? = null

                class Artwork {
                    @Json(name = "url")
                    var url:String = ""

                    @Json(name = "width")
                    var width:Int = 0

                    @Json(name = "height")
                    var height:Int= 0
                }

                class UserRating {
                    @Json(name = "value")
                    var value:String = ""

                    @Json(name = "ratingCount")
                    var ratingCount:Int = 0

                    @Json(name = "ratingCountList")
                    var ratingCountList:List<Int> = arrayListOf()
                }

                class Description {
                    @Json(name = "standard")
                    var standard:String = ""
                }

                class GenreResult {
                    @Json(name = "genreId")
                    var genreId:Int = 0

                    @Json(name = "name")
                    var name:String = ""
                }

                class ContentRatingsBySystemResult {
                    @Json(name = "riaa")
                    lateinit var riaa:RiaaResult

                    class RiaaResult {
                        @Json(name = "name")
                        var name:String = ""

                        @Json(name = "rank")
                        var rank:Int = 0
                    }
                }

                class ProductDvResultChildrenItem {
                    @Json(name = "name")
                    var name:String = ""

                    @Json(name = "artistName")
                    var artistName:String = ""

                    @Json(name = "collectionName")
                    var collectionName:String = ""

                    @Json(name = "url")
                    var url:String = ""

                    @Json(name = "feedUrl")
                    var feedUrl:String = ""

                    @Json(name = "id")
                    var id:Long = 0

                    @Json(name = "artistId")
                    var artistId:Long = 0

                    @Json(name = "releaseDate")
                    lateinit var releaseDate: LocalDate

                    @Json(name = "releaseDateTime")
                    lateinit var releaseDateTime:LocalDateTime

                    @Json(name = "podcastEpisodeGuid")
                    var podcastEpisodeGuid:String = ""

                    @Json(name = "genres")
                    var genres: List<GenreResult> = arrayListOf()

                    @Json(name = "contentRatingsBySystem")
                    lateinit var contentRatingsBySystem: ContentRatingsBySystemResult

                    @Json(name = "description")
                    lateinit var description: Description

                    @Json(name = "offers")
                    var offers: List<ProductDvResultChildrenItemOffers> = arrayListOf()

                    class ProductDvResultChildrenItemOffers
                    {
                        @Json(name = "download")
                        lateinit var download: ProductDvResultChildrenItemDownload

                        @Json(name = "assets")
                        var assets: List<ProductDvResultChildrenItemAsset> = arrayListOf()
                    }

                    class ProductDvResultChildrenItemDownload {
                        @Json(name = "url")
                        var url:String = ""

                        @Json(name = "type")
                        var type:String = ""
                    }

                    class ProductDvResultChildrenItemAsset {
                        @Json(name = "flavor") //standardAudio/sdVideo/sd480pVideo/720pHdVideo/1080pHdVideo
                        var flavor:String = ""

                        @Json(name = "extension")
                        var extension:String = ""

                        @Json(name = "duration")
                        var duration:Int = 0
                    }
                }
            }
        }
    }

    class PageDataResult {
        @Json(name = "fcStructure")
        lateinit var fcStructure: PageDataStructureResult

        class PageDataStructureResult {
            @Json(name = "id")
            var id: Long = 0

            @Json(name = "websiteUrl")
            var websiteUrl: String = ""

            @Json(name = "moreByArtist")
            var moreByArtist: List<Long> = ArrayList()

            @Json(name = "listenersAlsoBought")
            var listenersAlsoBought: List<Long> = ArrayList()
        }
    }
}