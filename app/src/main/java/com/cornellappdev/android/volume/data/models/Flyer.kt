package com.cornellappdev.android.volume.data.models

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class Flyer(
    val id: String,
    val title: String,
    val organization: Organization,
    val flyerURL: String?,
    val startDate: String,
    val endDate: String,
    val imageURL: String,
    val location: String,
    val categorySlug: String,
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
