package com.caldeirasoft.castly.domain.model


/**
 * Created by Edmond on 09/02/2018.
 */
class SectionWithCountEntity : SectionWithCount {
    override var QueueCount: Int = 0
    override var InboxCount: Int = 0
    override var FavoritesCount: Int = 0
    override var HistoryCount: Int = 0
}