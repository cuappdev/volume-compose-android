package com.cornellappdev.android.volume.data.models

import com.squareup.moshi.JsonClass
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@JsonClass(generateAdapter = true)
data class Flyer(
    val id: String,
    val title: String,
    val organizations: List<Organization>,
    val flyerURL: String,
    val startDate: String,
    val endDate: String,
    val imageURL: String,
    val location: String,
) : Comparable<Flyer> {
    val endDateTime: LocalDateTime =
        ZonedDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME)
            .withZoneSameInstant(ZoneId.of(ZonedDateTime.now().zone.id)).toLocalDateTime()
    val startDateTime: LocalDateTime =
        ZonedDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME)
            .withZoneSameInstant(ZoneId.of(ZonedDateTime.now().zone.id)).toLocalDateTime()

    override fun compareTo(other: Flyer): Int {
        return endDateTime.compareTo(other.endDateTime)
    }
}
