package com.example.volume_android_revamp



data class Article(
        var id: String,
        var articlURL: String,
        var data: String,
        var imageURL: String,
        var publication: Publication,
        var publicationSlug: String,
        var shoutouts: Float,
        var title: String,
        var nsfw: Boolean,
        var isTrending: Boolean,
        var trendiness: Float
        )

data class Publication (
        var id: String,
        var backgroundimageURL: String,
        var bio: String,
        var bioShort: String,
        var profileImageURL: String,
        var rssName: String,
        var rssURL: String,
        var slug: String,
        var shoutouts: Float,
        var websiteURL: String,
        var mostRecentArticle: Article,
        var numArticles: Float,
        var socials: Social
)

data class Social(
        var social: String,
        var URL: String
)
