package com.cornellappdev.volume.util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookmarkStatus(
    val status: FinalBookmarkStatus,
    val articleId: String
) : Parcelable

enum class FinalBookmarkStatus {
    BOOKMARKED_TO_UNBOOKMARKED, UNBOOKMARKED_TO_BOOKMARKED, UNCHANGED
}
