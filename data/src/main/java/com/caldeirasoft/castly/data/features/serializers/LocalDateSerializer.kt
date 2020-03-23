package com.caldeirasoft.castly.data.features.serializers

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import kotlinx.serialization.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor("org.threeten.bp.LocalDate", PrimitiveKind.STRING)

    private val df: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val dfZ: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'Z'")

    override fun deserialize(decoder: Decoder): LocalDate =
        decoder.decodeString().let { date ->
            val formatter = if (date.endsWith('Z')) dfZ else df
            LocalDate.parse(date, formatter)
        }

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(dfZ))
    }
}