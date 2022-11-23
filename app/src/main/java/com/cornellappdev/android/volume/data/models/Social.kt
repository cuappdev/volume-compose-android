package com.cornellappdev.android.volume.data.models

import com.cornellappdev.android.volume.R

/**
 * Model class for Socials on Volume
 *
 * @property social
 * @property url
 */
data class Social(
    val social: String,
    val url: String
) {
    companion object {
        val formattedSocialNameMap = mapOf(
            "insta" to "Instagram",
            "facebook" to "Facebook",
            "linkedin" to "LinkedIn",
            "twitter" to "Twitter",
            "youtube" to "YouTube"
        )
        val socialLogoMap =
            mapOf("Instagram" to R.drawable.ic_instagram, "Facebook" to R.drawable.ic_facebook)
    }
}
