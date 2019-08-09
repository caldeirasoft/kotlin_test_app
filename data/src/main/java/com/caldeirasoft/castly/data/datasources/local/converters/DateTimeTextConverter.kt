package com.caldeirasoft.castly.data.datasources.local.converters

import androidx.room.TypeConverter
import org.threeten.bp.LocalDateTime


class DateTimeTextConverter {
    @TypeConverter
    fun fromStringToLocalDateTime(value: String?): LocalDateTime? {
        return ThreeTenFormatter.dbStringToLocalDateTime(value)
    }

    @TypeConverter
    fun fromLocalDateTimeToString(value: LocalDateTime?): String? {
        return ThreeTenFormatter.localDateTimeToDBString(value)
    }
}