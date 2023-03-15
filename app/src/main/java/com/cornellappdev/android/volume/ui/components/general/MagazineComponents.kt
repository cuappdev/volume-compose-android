package com.cornellappdev.android.volume.ui.components.general

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cornellappdev.android.volume.data.models.Magazine
import com.cornellappdev.android.volume.ui.theme.GrayOne
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif

private const val TAG = "MagazineComponents"

@Composable
fun CreateMagazineColumn (
    magazine: Magazine
) {
    Log.d(TAG, "CreateMagazineColumn: Creating magazine column")
    Column (
        Modifier
            .padding(10.dp)
            .wrapContentHeight()
            .clickable {
                Log.d(TAG, "CreateArticleColumn: ${magazine.title} Clicked!")
                // TODO implement on click.
            }) {

        // Magazine image
        AsyncImage(
            model = magazine.publication.backgroundImageURL, // TODO replace with page 1 of PDF?
            modifier = Modifier
                .height(220.dp)
                .width(150.dp)
                .shadow(8.dp), contentDescription = null, contentScale = ContentScale.Crop
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
            text = magazine.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontFamily = lato,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(5.dp))
        // Shoutouts and time since published text
        Text(
            text = "${magazine.semester.uppercase()} • ${magazine.shoutouts.toInt()} shout-outs",
            fontFamily = lato,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            color = GrayOne
        )
    }
}