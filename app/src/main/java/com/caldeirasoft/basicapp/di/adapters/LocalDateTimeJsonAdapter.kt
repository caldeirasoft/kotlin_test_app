package com.caldeirasoft.basicapp.di.adapters

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class LocalDateTimeJsonAdapter : JsonAdapter<LocalDateTime>() {
    override fun fromJson(reader: JsonReader): LocalDateTime? {

        val dateTime = reader.nextString();
        val dateformater = if (dateTime!!.endsWith("Z")) {
            //ZonedDateTime.parse(dateTime).toLocalDateTime()
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        } else {
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
        }

        return LocalDateTime.parse(dateTime, dateformater)
    }

    override fun toJson(writer: JsonWriter, value: LocalDateTime?) {
        writer.value(value?.toString())
    }
}