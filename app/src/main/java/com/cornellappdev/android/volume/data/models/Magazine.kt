package com.cornellappdev.android.volume.data.models

/**
 * Model class for Magazines on Volume
 *
 * @property id The id associated in the database.
 * @property date The magazine publication date
 * @property semester The semester the magazine was published in.
 * @property pdfURL The URL linking to the magazine pdf.
 * @property publication The publication that published the magazine.
 * @property shoutouts The number of shoutouts the magazine has.
 * @property title The title of the magazine.
 * @property nsfw Whether or not the magazine has NFSW content.
 */
data class Magazine(
    val id: String,
    val date: String,
    val semester: String,
    val pdfURL: String,
    val imageURL: String,
    val publication: Publication,
    val shoutouts: Double,
    val title: String,
    val nsfw: Boolean = false,
)
