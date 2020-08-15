package com.caldeirasoft.castly.data.datasources.local.converters

import android.text.TextUtils
import androidx.room.TypeConverter
import com.caldeirasoft.castly.domain.model.entities.Artwork
import com.caldeirasoft.castly.domain.model.entities.Genre
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types.newParameterizedType
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.util.*
import java.util.Collections.emptyList


object DbTypeConverter {
    private val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

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

    @TypeConverter
    @JvmStatic
    fun dateToString(date: LocalDate?): String? {
        return ThreeTenFormatter.localDateToDBString(date)
    }

    @TypeConverter
    @JvmStatic
    fun stringToDate(dateAsString: String?): LocalDate? {
        return ThreeTenFormatter.dbStringToLocalDate(dateAsString)
    }

    @TypeConverter
    @JvmStatic
    fun dateTimeToString(date: LocalDateTime?): String? {
        return ThreeTenFormatter.localDateTimeToDBString(date)
    }

    @TypeConverter
    @JvmStatic
    fun stringToDateTime(dateAsString: String?): LocalDateTime? {
        return ThreeTenFormatter.dbStringToLocalDateTime(dateAsString)
    }

    @TypeConverter
    @JvmStatic
    fun fromArtwork(value: Artwork): String? {
        return moshi.adapter(Artwork::class.java).toJson(value)
    }

    @TypeConverter
    @JvmStatic
    fun toArtwork(valueAsString: String?): Artwork? {
        return valueAsString?.let { moshi.adapter(Artwork::class.java).fromJson(it) }
    }

    @TypeConverter
    @JvmStatic
    fun fromLongList(list: List<Long>): String {
        return TextUtils.join(",", list)
    }

    @TypeConverter
    @JvmStatic
    fun toLongList(value: String): List<Long> {
        return TextUtils.split(value, ",").toList().map { it.toLong() }
    }

    @TypeConverter
    @JvmStatic
    fun fromGenresList(value: List<Genre>): String {
        return moshi.adapter<List<Genre>>(newParameterizedType(List::class.java, Genre::class.java))
                .toJson(value)
    }

    @TypeConverter
    @JvmStatic
    fun toGenresList(value:String): List<Genre> {
        return moshi.adapter<List<Genre>>(newParameterizedType(List::class.java, Genre::class.java))
                .fromJson(value) ?: emptyList()
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