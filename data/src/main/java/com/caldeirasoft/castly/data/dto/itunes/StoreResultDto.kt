package com.caldeirasoft.castly.data.dto.itunes

import com.squareup.moshi.Json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

/**
 * Created by Edmond on 12/02/2018.
 */
class StoreResultDto {
    @Json(name = "storePlatformData")
    lateinit var storePlatformData: StorePlatformDataResult

    @Json(name = "pageData")
    lateinit var pageData: PageDataResult

    class StorePlatformDataResult {
        @Json(name = "lockup")
        lateinit var lockup: LockupResult

        class LockupResult {
            @Json(name = "results")
            lateinit var results: Map<String, LookupItemDto>
        }
    }

    class PageDataResult {
        @Json(name = "fcStructure")
        lateinit var fcStructure: PageDataStructureResult

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

                @Json(name = "children")
                var children: List<PageDataModelResult> = ArrayList()

                @Json(name = "artwork")
                lateinit var artwork: Artwork

                @Json(name = "link")
                var link: ContentId = ContentId()

                @Json(name = "content")
                var content: List<ContentId> = ArrayList()

                class Artwork {
                    @Json(name = "url")
                    var url: String = ""

                    @Json(name = "bgColor")
                    var bgColor: String = ""

                    @Json(name = "textColor1")
                    var textColor1: String = ""
                }

                class ContentId {
                    @Json(name = "type")
                    var type: String = ""

                    @Json(name = "contentId")
                    var contentId: String = ""

                    @Json(name = "url")
                    var url: String = ""
                }
            }
        }
    }
}