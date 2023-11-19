package com.cornellappdev.android.volume.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.cornellappdev.android.volume.ui.states.ResponseState

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

/**
 * Generic function to handle applying a transformation function to two response states.
 * If either response state has an error, it returns an error response state.
 * If either response state is loading, it returns a loading response state.
 * If both response states are successful, it applies `transform` to a and b and returns a successful
 * response state with transform applied.
 *
 * @param a the first response state
 * @param b the second response state
 * @param transform the transformation function that is applied to (a, b) if both a and b are successful.
 */
fun <T1, T2, R> transformWithResponseState(
    a: ResponseState<T1>,
    b: ResponseState<T2>,
    transform: (a: T1, b: T2) -> R,
): ResponseState<R> {
    if (a is ResponseState.Error || b is ResponseState.Error) {
        return ResponseState.Error()
    }
    if (a is ResponseState.Loading || b is ResponseState.Loading) {
        return ResponseState.Loading
    }
    if (a is ResponseState.Success && b is ResponseState.Success) {
        return ResponseState.Success(transform(a.data, b.data))
    }

    // The code should never reach here, we've covered all cases. I'd rather not throw an exception
    // though because that could crash the app.
    return ResponseState.Error()
}