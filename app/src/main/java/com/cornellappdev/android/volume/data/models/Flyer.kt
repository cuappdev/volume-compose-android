package com.cornellappdev.android.volume.data.models

import com.squareup.moshi.JsonClass

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
)
