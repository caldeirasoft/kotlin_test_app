package com.caldeirasoft.castly.data.dto.itunes

import com.squareup.moshi.Json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

/**
 * Created by Edmond on 12/02/2018.
 */
class GroupingPageResultDto {
    @Json(name = "storePlatformData")
    lateinit var storePlatformData: StorePlatformDataResult

    @Json(name = "pageData")
    lateinit var pageData: PageDataResult

    class StorePlatformDataResult {
        @Json(name = "lockup")
        lateinit var lockup: LockupResult

        class LockupResult {
            @Json(name = "results")
            lateinit var results: Map<Long, LookupItemDto>
        }
    }

    class PageDataResult {
        @Json(name = "componentName")
        var componentName: String = ""

        @Json(name = "fcStructure")
        lateinit var fcStructure: PageDataStructureResult

        @Json(name = "genreId")
        var genreId: Int = 0

        @Json(name = "categoryList")
        lateinit var categoryList: CategoryListResult

        class CategoryListResult {
            @Json(name = "name")
            var name: String = ""

            @Json(name = "genreId")
            var genreId: Int = 0

            @Json(name = "children")
            var children: List<GenreDto> = arrayListOf()
        }

        class PageDataStructureResult {
            @Json(name = "model")
            lateinit var model: PageDataModelResult

            class PageDataModelResult {
                @Json(name = "fcKind")
                var fcKind: Int = 0

                @Json(name = "name")
                var name: String = ""

                @Json(name = "token")
                var token: String = ""

                @Json(name = "designLabel")
                var designLabel: String? = null

                @Json(name = "type")
                var type: String = ""

                @Json(name = "adamId")
                var adamId: Long = 0

                @Json(name = "children")
                var children: List<PageDataModelResult> = arrayListOf()

                @Json(name = "artwork")
                lateinit var artwork: ArtworkDto

                @Json(name = "link")
                var link: Link = Link()

                @Json(name = "content")
                var content: List<ContentId> = arrayListOf()

                class Link {
                    @Json(name = "type")
                    var type: String = ""

                    @Json(name = "label")
                    var label: String = ""

                    @Json(name = "contentId")
                    var contentId: Long = 0

                    @Json(name = "url")
                    var url: String = ""

                    @Json(name = "kindIds")
                    var kindIds: List<Int> = arrayListOf()
                }

                class ContentId {
                    @Json(name = "type")
                    var type: String = ""

                    @Json(name = "contentId")
                    var contentId: Long = 0

                    @Json(name = "target")
                    var target: String = ""
                }
            }
        }
    }
}