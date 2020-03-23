package com.caldeirasoft.castly.data.features.serializers

import com.squareup.moshi.JsonAdapter
import kotlinx.serialization.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor("org.threeten.bp.LocalDateTime", PrimitiveKind.STRING)

    private val df: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    private val dfZ: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

    override fun deserialize(decoder: Decoder): LocalDateTime =
            decoder.decodeString().let { date ->
                val formatter = if (date.endsWith('Z')) dfZ else df
                LocalDateTime.parse(date, formatter)
            }

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.format(dfZ))
    }
}