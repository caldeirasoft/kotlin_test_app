package com.caldeirasoft.castly.data.dto.itunes

import com.caldeirasoft.castly.domain.model.entities.Artwork
import com.squareup.moshi.Json

/**
 * Created by Edmond on 12/02/2018.
 */
class ArtworkDto {
    @Json(name = "url")
    var url: String = ""

    @Json(name = "width")
    var width: Int = 0

    @Json(name = "height")
    var height: Int = 0

    @Json(name = "textColor1")
    var textColor1: String? = null

    @Json(name = "textColor2")
    var textColor2: String? = null

    @Json(name = "textColor3")
    var textColor3: String? = null

    @Json(name = "textColor4")
    var textColor4: String? = null

    @Json(name = "bgColor")
    var bgColor: String? = null

    @Json(name = "hasAlpha")
    var hasAlpha: Boolean? = null

    fun toArtwork() =
            Artwork(
                    width = width,
                    height = height,
                    url = url,
                    bgColor = bgColor,
                    textColor1 = textColor2,
                    textColor2 = textColor4)

    companion object {
        fun toArtwork(dto: ArtworkDto) =
                Artwork(
                        width = dto.width,
                        height = dto.height,
                        url = dto.url,
                        bgColor = dto.bgColor,
                        textColor1 = dto.textColor1,
                        textColor2 = dto.textColor4)
    }
}