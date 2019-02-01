package com.caldeirasoft.basicapp.presentation.preferences

import com.caldeirasoft.basicapp.presentation.model.EnumPodcastLayout
import com.chibatching.kotpref.KotprefModel

object UserPref : KotprefModel()
{
    var podcastLayout: Int by intPref(EnumPodcastLayout.LIST.value)
}
