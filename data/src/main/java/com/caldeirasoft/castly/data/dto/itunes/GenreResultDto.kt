package com.caldeirasoft.castly.data.dto.itunes

import com.squareup.moshi.Json
import java.util.*

/**
 * Created by Edmond on 12/02/2018.
 */
class GenreResultDto {
    @Json(name = "storePlatformData")
    lateinit var storePlatformData: StorePlatformDataResult

    @Json(name = "pageData")
    lateinit var pageData: PageDataResult

    class StorePlatformDataResult {
        @Json(name = "lockup")
        lateinit var lockup: LockupResult

        class LockupResult {
            @Json(name = "results")
            lateinit var results:Map<Long, LookupItemDto>
        }
    }

    class PageDataResult {
        @Json(name = "fcStructure")
        lateinit var fcStructure: PageDataStructureResult

        @Json(name = "genreId")
        var genreId:Int = 0

        @Json(name = "categoryList")
        lateinit var categoryList: CategoryListResult


        class CategoryListResult {
            @Json(name = "name")
            var name:String = ""

            @Json(name = "genreId")
            var genreId:Int = 0

            @Json(name = "children")
            var children:List<GenreResult> = arrayListOf()

            class GenreResult {
                @Json(name = "genreId")
                var genreId:Int = 0

                @Json(name = "name")
                var name:String = ""
            }
        }

        class PageDataStructureResult {
            @Json(name = "model")
            lateinit var model: PageDataModelResult

            class PageDataModelResult {
                @Json(name = "fcKind")
                var fcKind:Int = 0

                @Json(name = "name")
                var name:String = ""

                @Json(name = "token")
                var token:String = ""

                @Json(name = "adamId")
                var adamId:Long = 0

                @Json(name = "children")
                var children: List<PageDataModelResult> = ArrayList()

                @Json(name = "artwork")
                lateinit var artwork: Artwork

                @Json(name = "link")
                var link: ContentId = ContentId()

                @Json(name = "content")
                var content:List<ContentId> = ArrayList()

                class Artwork {
                    @Json(name = "url")
                    var url:String = ""

                    @Json(name = "bgColor")
                    var bgColor:String = ""

                    @Json(name = "textColor1")
                    var textColor1:String = ""
                }

                class ContentId {
                    @Json(name = "type")
                    var type:String = ""

                    @Json(name = "contentId")
                    var contentId:Long = 0

                    @Json(name = "url")
                    var url:String = ""

                    @Json(name = "target")
                    var target:String = ""
                }
            }
        }
    }
}