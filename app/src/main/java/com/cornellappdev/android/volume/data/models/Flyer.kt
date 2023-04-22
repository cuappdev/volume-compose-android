package com.cornellappdev.android.volume.data.models

data class Flyer(
    val id: String,
    val flyerURL: String,
    val date: String,
    val imageURL: String,
    val organization: Organization,
    val location: String,
    val organizationSlug: String,
    val shoutouts: Double,
    val title: String,
    val nsfw: Boolean,
    val isTrending: Boolean,
)
