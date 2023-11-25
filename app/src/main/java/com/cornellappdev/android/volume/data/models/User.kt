package com.cornellappdev.android.volume.data.models

/**
 * Model class for Users on Volume
 *
 * @property uuid
 * @property followedPublicationSlugs
 * @property followedOrganizationSlugs
 * @property weeklyDebrief
 */
data class User(
    val uuid: String,
    val followedPublicationSlugs: List<String>,
    val followedOrganizationSlugs: List<String>,
    val weeklyDebrief: WeeklyDebrief? = null,
)
