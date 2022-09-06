package com.cornellappdev.volume.data.models

/**
 * Model class pertaining to WeeklyDebrief
 *
 * @property uuid
 * @property readArticlesIDs
 * @property randomArticlesIDs
 * @property creationDate
 * @property expirationDate
 * @property numShoutouts
 * @property numBookmarkedArticles
 * @property numReadArticles
 */
data class WeeklyDebrief(
    val uuid: String,
    val readArticlesIDs: List<String>,
    val randomArticlesIDs: List<String>,
    val creationDate: String,
    val expirationDate: String,
    val numShoutouts: Int,
    val numBookmarkedArticles: Int,
    val numReadArticles: Int
)
