package com.caldeirasoft.castly.data.models.itunes

import com.caldeirasoft.castly.domain.model.entities.Artwork
import com.caldeirasoft.castly.domain.model.entities.Podcast
import com.caldeirasoft.castly.domain.model.itunes.MultiRoomPageData

/**
 * Created by Edmond on 09/02/2018.
 */
class MultiRoomPageDataEntity(
        pageTitle: String,
        id: Long,
        url: String,
        artwork: Artwork?)
    : MultiRoomPageData(pageTitle, id, url, artwork) {
    override var description: String? = null

    constructor() : this("", 0, "", null)
}