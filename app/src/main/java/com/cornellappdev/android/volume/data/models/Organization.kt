package com.cornellappdev.android.volume.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Organization(
    val id: String,
    val name: String,
    val slug: String,
    val type: String
)

val organizationTypes = listOf("all", "academic", "art", "awareness", "comedy", "cultural",
    "dance", "foodDrinks", "greekLife", "music", "socialJustice", "spiritual", "sports")