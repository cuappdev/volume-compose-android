package com.cornellappdev.android.volume.util

inline fun <T : Any, R : Any> letIfAllNotNull(vararg arguments: T?, block: (List<T>) -> R): R? {
    return if (arguments.all { it != null }) {
        block(arguments.filterNotNull())
    } else null
}
