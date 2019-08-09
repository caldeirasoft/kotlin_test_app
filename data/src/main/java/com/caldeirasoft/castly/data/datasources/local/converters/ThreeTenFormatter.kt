package com.caldeirasoft.castly.data.datasources.local.converters

import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter


object ThreeTenFormatter {
    // ========== JSR 310 ==========
    // JSR 310 LocalDateTime - long
    fun localDateTimeToLong(d: LocalDateTime?): Long? =
            d?.let { d.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() }

    fun longToLocalDateTime(l: Long?): LocalDateTime? =
            l?.let { Instant.ofEpochMilli(l).atZone(ZoneId.systemDefault()).toLocalDateTime() }


    fun localDateTimeToLongUtc(d: LocalDateTime?): Long? =
            d?.toInstant(ZoneOffset.UTC)?.toEpochMilli()


    fun longToLocalDateTimeUtc(l: Long?): LocalDateTime? =
            l?.let { Instant.ofEpochMilli(l).atZone(ZoneOffset.UTC).toLocalDateTime() }

    // JSR 310 LocalDateTime - String

    // Date and Time
    fun localDateTimeToDBString(d: LocalDateTime?): String? =
            d?.let { DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(d) }

    fun dbStringToLocalDateTime(text: String?): LocalDateTime? {
        if (text != null && text.isNotEmpty() && text != "null") {
            try {
                return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            } catch (ex: Exception) {
                throw IllegalArgumentException("Cannot parse date time text: " + text, ex)
            }

        } else {
            return null
        }
    }

    // Date only
    fun localDateToDBString(d: LocalDate?): String? =
            d?.let { DateTimeFormatter.ISO_LOCAL_DATE.format(d) }

    fun dbStringToLocalDate(text: String?): LocalDate? {
        if (text != null && text.isNotEmpty() && text != "null") {
            try {
                return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE)
            } catch (ex: Exception) {
                throw IllegalArgumentException("Cannot parse date text: " + text, ex)
            }

        } else {
            return null
        }
    }

    // Time only
    fun localTimeToDBString(d: LocalTime?): String? =
            d?.let { DateTimeFormatter.ISO_LOCAL_TIME.format(d) }

    fun dbStringToLocalTime(text: String?): LocalTime? {
        if (text != null && text.isNotEmpty() && text != "null") {
            try {
                return LocalTime.parse(text, DateTimeFormatter.ISO_LOCAL_TIME)
            } catch (ex: Exception) {
                throw IllegalArgumentException("Cannot parse time text: " + text, ex)
            }

        } else {
            return null
        }
    }
}