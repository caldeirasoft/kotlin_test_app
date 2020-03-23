package com.caldeirasoft.castly.data.dto.itunes

import com.caldeirasoft.castly.data.features.serializers.LocalDateTimeSerializer
import com.squareup.moshi.Json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.threeten.bp.LocalDateTime

/**
 * Created by Edmond on 12/02/2018.
 */
class SearchResultDto {
    @SerialName("results")
    var results: List<Result> = ArrayList()

    class Result {
        @SerialName("artistName")
        var artistName: String = ""

        @SerialName("trackName")
        var trackName: String = ""

        @SerialName("feedUrl")
        var feedUrl: String = ""

        @SerialName("trackId")
        var trackId: Long = 0

        @SerialName("artworkUrl100")
        var artworkUrl100: String = ""

        @SerialName("artworkUrl600")
        var artworkUrl600: String = ""

        @SerialName("releaseDate")
        @Serializable(with = LocalDateTimeSerializer::class)
        var releaseDate: LocalDateTime = LocalDateTime.MIN

        @SerialName("trackCount")
        var trackCount: Int = 0

        @SerialName("contentAdvisoryRating")
        var contentAdvisoryRating: String = "" //clean/explicit

    }
}