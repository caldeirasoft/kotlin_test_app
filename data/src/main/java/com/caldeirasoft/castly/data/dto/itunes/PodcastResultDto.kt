package com.caldeirasoft.castly.data.dto.itunes

import com.caldeirasoft.castly.data.features.serializers.LocalDateSerializer
import com.caldeirasoft.castly.data.features.serializers.LocalDateTimeSerializer
import com.squareup.moshi.Json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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
        var productDv: ProductDvResult = ProductDvResult()

        class ProductDvResult {
            @Json(name = "results")
            lateinit var results: Map<Long, PodcastResultItemDto>
        }
    }

    class PageDataResult {
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