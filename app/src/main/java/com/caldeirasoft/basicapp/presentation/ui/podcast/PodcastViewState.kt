package com.caldeirasoft.basicapp.presentation.ui.podcast

import com.caldeirasoft.castly.domain.model.Podcast

data class PodcastViewState(
    val items: List<Podcast>? = arrayListOf(),
    val isLoading: Boolean = false
)