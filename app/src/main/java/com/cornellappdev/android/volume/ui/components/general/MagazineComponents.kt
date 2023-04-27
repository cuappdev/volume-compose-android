package com.cornellappdev.android.volume.ui.components.general

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.volume.R
import com.cornellappdev.android.volume.data.models.Magazine
import com.cornellappdev.android.volume.ui.theme.GrayOne
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif
import com.rizzi.bouquet.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


private const val TAG = "MagazineComponents"
@OptIn(ExperimentalComposeUiApi::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun CreateMagazineColumn (
    magazine: Magazine,
    onMagazineClick: (magazine: Magazine) -> Unit,
    isBookmarked: Boolean = false
) {
    var imgBitmap: MutableState<Bitmap?> = remember { mutableStateOf( null) }
    LaunchedEffect(key1 = magazine.pdfURL) {
        imgBitmap.value = pdfUrlToBitmap(magazine.pdfURL)
    }

    Column (
        Modifier
            .padding(10.dp)
            .wrapContentHeight()
            .clickable {
                onMagazineClick(magazine)
            }) {
        // Magazine image
        when (val bitmap = imgBitmap.value) {
            null -> {
                Box (modifier = Modifier.height(200.dp).width(150.dp).shimmerEffect())
            }
            else -> {
                Image(
                    bitmap = bitmap.asImageBitmap(), contentDescription = null, modifier = Modifier
                        .width(150.dp)
                        .height(200.dp)
                        .shadow(8.dp)
                )
            }
        }

        // Magazine publisher text
        Text(
            modifier = Modifier.padding(top = 16.dp, bottom = 2.dp),
            text = magazine.publication.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontFamily = notoserif,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
        )
        // Magazine title text
        Text(
            modifier = Modifier.width(150.dp),
            text = magazine.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontFamily = lato,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(5.dp))
        // Shoutouts and time since published text
        Row {
            Text(
                text = "${magazine.semester.uppercase()} • ${
                    pluralStringResource(
                        R.plurals.shoutout_count,
                        magazine.shoutouts.toInt(),
                        magazine.shoutouts.toInt()
                    )
                }",
                fontFamily = lato,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                color = GrayOne
            )
            if (isBookmarked) {
                Icon(
                    imageVector = Icons.Filled.Bookmark,
                    contentDescription = null,
                    tint = VolumeOrange,
                    modifier = Modifier
                        .size(17.dp)
                        .padding(start = 6.dp)
                        .align(Alignment.Bottom)
                )
            }
        }
    }
}

suspend fun pdfUrlToBitmap(url: String): Bitmap? = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()

    var bitmap: Bitmap? = null
    var pdfFile: File? = null
    var fileOutputStream: FileOutputStream? = null
    var fileDescriptor: ParcelFileDescriptor? = null
    var pdfRenderer: PdfRenderer? = null

    try {
        val response = client.newCall(request).execute()
        val inputStream = response.body?.byteStream()

        if (inputStream != null) {
            pdfFile = File.createTempFile("temp_pdf", ".pdf")
            fileOutputStream = FileOutputStream(pdfFile)

            val buffer = ByteArray(8 * 1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                fileOutputStream.write(buffer, 0, bytesRead)
            }

            fileOutputStream.flush()
            fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            pdfRenderer = PdfRenderer(fileDescriptor)

            val pageCount = pdfRenderer.pageCount
            if (pageCount > 0) {
                val firstPage = pdfRenderer.openPage(0)
                val width = firstPage.width
                val height = firstPage.height

                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                firstPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                firstPage.close()
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        fileOutputStream?.close()
        pdfRenderer?.close()
        fileDescriptor?.close()
        pdfFile?.delete()
    }

    bitmap
}