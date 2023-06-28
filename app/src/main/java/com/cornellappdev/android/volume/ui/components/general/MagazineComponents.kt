package com.cornellappdev.android.volume.ui.components.general

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cornellappdev.android.volume.R
import com.cornellappdev.android.volume.data.models.Magazine
import com.cornellappdev.android.volume.ui.theme.GrayOne
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif
import com.rizzi.bouquet.*


private const val TAG = "MagazineComponents"

@OptIn(ExperimentalComposeUiApi::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun CreateMagazineColumn(
    magazine: Magazine,
    onMagazineClick: (magazine: Magazine) -> Unit,
    isBookmarked: Boolean = false,
) {
    Column(
        Modifier
            .padding(10.dp)
            .wrapContentHeight()
            .clickable {
                onMagazineClick(magazine)
            }) {
        // Magazine image
        AsyncImage(
            model = magazine.imageURL, contentDescription = null, modifier = Modifier
                .width(150.dp)
                .height(200.dp)
                .shadow(8.dp)
        )

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