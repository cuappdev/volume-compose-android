package com.cornellappdev.android.volume.util

class FlyerConstants {
    companion object {
        const val CATEGORY_SLUGS =
            "all,academic,art,awareness,comedy,cultural,dance,foodDrink,greekLife,music,socialJustice,spiritual,sports"

        // Maps the category slugs to strings that are viewable on the app.
        val FORMATTED_TAGS = CATEGORY_SLUGS.split(",").map { s: String ->
            // If the slug is just a single word, capitalize it.
            if (s == s.lowercase()) {
                s.replaceFirstChar { c -> c.uppercase() }
                // If the slug is multiple words, split it on uppercase characters and join them, capitalizing each word.
            } else {
                s.split(Regex("(?<!^)(?=[A-Z])"))
                    .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
            }
        }
    }
}