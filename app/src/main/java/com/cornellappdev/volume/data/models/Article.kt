package com.cornellappdev.volume.data.models

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.pluralStringResource
import com.cornellappdev.volume.R
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

/**
 * Model class for Articles on Volume
 *
 * @property id
 * @property title
 * @property articleURL
 * @property imageURL
 * @property publication
 * @property date
 * @property shoutouts
 * @property nsfw
 */
data class Article(
    val id: String,
    val title: String,
    val articleURL: String,
    val imageURL: String,
    val publication: Publication,
    val date: String,
    val shoutouts: Double,
    val nsfw: Boolean = false
) {
    companion object {
        fun sortByDate(articles: MutableList<Article>) {
            articles.sortWith(compareByDescending { article ->
                article.date
            })
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun getTimeSinceArticlePublished(): String {
        val format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val datePublished = LocalDateTime.parse(date, format)
        val dur = Duration.between(datePublished, LocalDateTime.now())
        val hours = abs(dur.toHours()).toInt()
        val days = abs(dur.toDays()).toInt()

        val weeks = days / 7
        val months = days / 30
        val years = days / 365

        return when {
            days < 1 -> {
                pluralStringResource(R.plurals.x_h_ago, hours, hours)
            }
            days in 1..6 -> {
                pluralStringResource(R.plurals.x_days_ago, days, days)
            }
            days in 7..29 -> {
                pluralStringResource(R.plurals.x_weeks_ago, weeks, weeks)
            }
            days in 30..364 -> {
                pluralStringResource(R.plurals.x_months_ago, months, months)
            }
            else -> {
                pluralStringResource(R.plurals.x_years_ago, years, years)
            }
        }
    }
}
