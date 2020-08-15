package com.caldeirasoft.castly.data.dto.itunes

import com.caldeirasoft.castly.domain.model.entities.Genre
import com.squareup.moshi.Json

/**
 * Created by Edmond on 12/02/2018.
 */
class GenreDto {
    @Json(name = "genreId")
    var genreId: Int = 0

    @Json(name = "name")
    var name: String = ""

    @Json(name = "url")
    var url: String = ""

    fun toGenre() =
            Genre(id = genreId,
                    name = name,
                    url = url)

    companion object {
        fun toGenre(dto: GenreDto) =
                Genre(id = dto.genreId,
                        name = dto.name,
                        url = dto.url)
    }
}