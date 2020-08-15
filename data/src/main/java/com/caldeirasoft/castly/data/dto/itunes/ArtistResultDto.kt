package com.caldeirasoft.castly.data.dto.itunes

import com.squareup.moshi.Json
import java.util.*

/**
 * Created by Edmond on 12/02/2018.
 */
class ArtistResultDto {
    @Json(name = "storePlatformData")
    lateinit var storePlatformData: StorePlatformDataResult

    @Json(name = "pageData")
    lateinit var pageData: PageDataResult

    class StorePlatformDataResult {
        @Json(name = "lockup")
        var lockup: LockupResult? = null

        class LockupResult {
            @Json(name = "results")
            lateinit var results: Map<Long, LookupItemDto>
        }
    }

    class PageDataResult {
        @Json(name = "adamIds")
        var adamIds: List<Long> = ArrayList()

        @Json(name = "segments")
        var contentData: List<ContentData> = ArrayList()

        @Json(name = "artist")
        lateinit var artist: Artist

        @Json(name = "uber")
        var uber: UberResult? = null

        class UberResult {
            @Json(name = "description")
            var description: String? = null

            @Json(name = "masterArt")
            var masterArt: List<ArtworkDto> = ArrayList()
        }

        class Artist {
            @Json(name = "adamId")
            var adamId: Long = 0

            @Json(name = "name")
            var name: String = ""

            @Json(name = "url")
            var url: String = ""

            @Json(name = "genres")
            var genres: List<GenreDto> = arrayListOf()
        }

        class ContentData {
            @Json(name = "title")
            var title: String = ""

            @Json(name = "chunkId")
            var chunkId: Long? = null

            @Json(name = "dkId")
            var dkId: Int? = null

            @Json(name = "adamIds")
            var adamIds: List<Long> = ArrayList()
        }
    }
}