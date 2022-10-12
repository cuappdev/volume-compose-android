package com.cornellappdev.volume.data.models

/**
 * Model class for Users on Volume
 *
 * @property uuid
 * @property followedPublicationSlugs
 * @property weeklyDebrief
 */
data class User(
    val uuid: String,
    val followedPublicationSlugs: List<String>,
    val weeklyDebrief: WeeklyDebrief? = null
)
