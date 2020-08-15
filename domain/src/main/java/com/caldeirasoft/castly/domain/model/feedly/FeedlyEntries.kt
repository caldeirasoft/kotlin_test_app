package com.caldeirasoft.castly.domain.model.feedly

import com.caldeirasoft.castly.domain.model.entities.Episode

data class FeedlyEntries(
        var data:List<Episode>,
        var continuation: String?)