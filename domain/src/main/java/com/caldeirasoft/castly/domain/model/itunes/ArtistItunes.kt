package com.caldeirasoft.castly.domain.model.itunes

import com.caldeirasoft.castly.domain.model.entities.Artwork
import com.caldeirasoft.castly.domain.model.entities.Genre
import kotlinx.serialization.Serializable

/**
 * Created by Edmond on 09/02/2018.
 */
@Serializable
class ArtistItunes {
    var artistName: String = ""
    var id: Long = 0
    var description: String? = null
    var url: String = ""
    var artwork: Artwork? = null
    var genres: List<Genre> = arrayListOf()
}