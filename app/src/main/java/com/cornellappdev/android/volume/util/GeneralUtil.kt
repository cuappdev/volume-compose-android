package com.cornellappdev.android.volume.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

inline fun <T : Any, R : Any> letIfAllNotNull(vararg arguments: T?, block: (List<T>) -> R): R? {
    return if (arguments.all { it != null }) {
        block(arguments.filterNotNull())
    } else null
}

fun deriveFileName(uri: Uri, context: Context): String? {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    if (cursor != null && cursor.moveToFirst()) {
        val colIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (colIndex != -1) {
            val filename = cursor.getString(colIndex)
            cursor.close()
            return filename
        }
        cursor.close()
    }
    cursor?.close()
    return null
}