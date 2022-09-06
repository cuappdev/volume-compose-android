package com.cornellappdev.volume.data.models

/**
 * Model class for Users on Volume
 *
 * @property uuid
 * @property followedPublicationIDs
 * @property weeklyDebrief
 */
data class User(
    val uuid: String,
    val followedPublicationIDs: List<String>,
    val weeklyDebrief: WeeklyDebrief? = null
)
