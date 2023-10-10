package com.cornellappdev.android.volume.data.models

data class Organization(
    val name: String,
    val id: String,
    val categorySlug: String,
    val websiteURL: String,
    val backgroundImageURL: String? = null,
    val bio: String? = null,
)

val organizationTypes = listOf(
    "all", "academic", "art", "awareness", "comedy", "cultural",
    "dance", "foodDrinks", "greekLife", "music", "socialJustice", "spiritual", "sports"
)