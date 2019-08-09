package com.caldeirasoft.castly.data.dto.itunes

import com.squareup.moshi.Json
import java.util.*

/**
 * Created by Edmond on 12/02/2018.
 */
class MultiRoomResultDto {
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
        @Json(name = "pageTitle")
        var pageTitle:String = ""

        @Json(name = "description")
        var description:String = ""

        @Json(name = "adamId")
        var id:Long = 0

        @Json(name = "segments")
        var segments:List<SegmentResult> = ArrayList()

        @Json(name = "uber")
        lateinit var uber:UberResult


        class UberResult {
            @Json(name = "description")
            var description:String = ""

            @Json(name = "masterArt")
            var masterArt:List<ArtworkDto> = ArrayList()
        }


        class SegmentResult {
            @Json(name = "title")
            var title:String = ""

            @Json(name = "adamId")
            var adamId:Long = 0

            @Json(name = "adamIds")
            var adamIds:List<Long> = ArrayList()
        }
    }
}