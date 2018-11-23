package com.caldeirasoft.basicapp.data.db

import androidx.room.TypeConverter

import java.util.*

object DbTypeConverter {
    @TypeConverter
    @JvmStatic
    fun dateToLong(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    @JvmStatic
    fun longToDate(dateAsLong: Long?): Date? {
        return dateAsLong?.let { Date(it) }
    }

    /*
    @TypeConverter
    @JvmStatic
    fun listStringToString(list: MutableList<String>): String {
        return TextUtils.join(",", list)
    }

    @TypeConverter
    @JvmStatic
    fun stringBackToList(lists: String): MutableList<String> {
        return ArrayUtils.toArrayList(TextUtils.split(lists, ","))
    }
    */
}