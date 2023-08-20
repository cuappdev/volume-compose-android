package com.cornellappdev.android.volume.data.models

import com.squareup.moshi.JsonClass
import java.time.LocalDateTime
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
        LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME)
    val startDateTime: LocalDateTime =
        LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME)

    override fun compareTo(other: Flyer): Int {
        return endDateTime.compareTo(other.endDateTime)
    }
}
