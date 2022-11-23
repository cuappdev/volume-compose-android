package com.cornellappdev.android.volume.data.models

/**
 * Model class for Publications on Volume
 *
 * @property backgroundImageURL
 * @property bio
 * @property name
 * @property profileImageURL
 * @property rssName
 * @property rssURL
 * @property slug
 * @property shoutouts
 * @property websiteURL
 * @property contentTypes
 * @property mostRecentArticle
 * @property socials
 */
data class Publication(
    val backgroundImageURL: String,
    val bio: String,
    val name: String,
    val profileImageURL: String,
    val rssName: String,
    val rssURL: String? = null,
    val slug: String,
    val shoutouts: Double,
    val websiteURL: String,
    val contentTypes: List<ContentType>,
    val numArticles: Double,
    val mostRecentArticle: Article? = null,
    val socials: List<Social>
)

enum class ContentType{
    MAGAZINES, ARTICLES
}
