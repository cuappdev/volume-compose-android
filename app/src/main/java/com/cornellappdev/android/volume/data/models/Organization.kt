package com.cornellappdev.android.volume.data.models

data class Organization(
    val id: String,
    val backgroundImageURL: String,
    val bio: String,
    val bioShort: String,
    val name: String,
    val profileImageUrl: String,
    val rssName: String,
    val rssURL: String,
    val slug: String,
    val shoutouts: String,
    val categorySlug: String,
    val websiteURL: String,
    val contentTypes: List<String>
)
