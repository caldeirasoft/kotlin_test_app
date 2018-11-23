package com.caldeirasoft.basicapp.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.caldeirasoft.basicapp.BuildConfig
import com.caldeirasoft.basicapp.data.enum.EnumPodcastLayout
import com.chibatching.kotpref.KotprefModel

object UserPref : KotprefModel()
{
    var podcastLayout: Int by intPref(EnumPodcastLayout.LIST.value)
}
