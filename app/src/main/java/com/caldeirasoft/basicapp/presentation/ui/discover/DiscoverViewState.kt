package com.caldeirasoft.basicapp.presentation.ui.discover

import com.caldeirasoft.castly.domain.model.itunes.StoreData

data class DiscoverViewState (
        val isLoading: Boolean = false,
        val storeData: StoreData? = null)