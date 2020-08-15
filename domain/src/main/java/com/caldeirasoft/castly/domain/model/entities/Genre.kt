package com.caldeirasoft.castly.domain.model.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Genre(val id: Int,
                 val name: String,
                 val url: String) : Parcelable
