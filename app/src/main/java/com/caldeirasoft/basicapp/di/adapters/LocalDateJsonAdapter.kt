package com.caldeirasoft.basicapp.di.adapters

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class LocalDateJsonAdapter : JsonAdapter<LocalDate>() {
    override fun fromJson(reader: JsonReader?): LocalDate? {

        val dateTime = reader?.nextString();
        val dateformater = if (dateTime!!.endsWith("Z")) {
            //ZonedDateTime.parse(dateTime).toLocalDateTime()
            DateTimeFormatter.ofPattern("yyyy-MM-dd'Z'")
        } else {
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
        }

        return LocalDate.parse(dateTime, dateformater)
    }

    override fun toJson(writer: JsonWriter?, value: LocalDate?) {
        writer?.value(value?.toString())
    }
}