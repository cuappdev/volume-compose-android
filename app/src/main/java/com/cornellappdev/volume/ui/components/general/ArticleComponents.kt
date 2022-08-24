package com.cornellappdev.volume.ui.components.general

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cornellappdev.volume.R
import com.cornellappdev.volume.data.models.Article
import com.cornellappdev.volume.ui.theme.GrayOne
import com.cornellappdev.volume.ui.theme.lato
import com.cornellappdev.volume.ui.theme.notoserif

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreateHorizontalArticleRow(article: Article) {
    val timeSincePublished = article.getTimeSinceArticlePublished()
    val shoutouts = article.shoutouts.toInt()
    Row(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .height(100.dp)
                .weight(1f)
                .padding(end = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    modifier = Modifier.padding(bottom = 2.dp, end = 20.dp),
                    text = article.publication.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = notoserif,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
                Text(
                    text = article.title,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = lato,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Text(
                text = "$timeSincePublished · ${
                    pluralStringResource(
                        R.plurals.shoutout_count,
                        shoutouts,
                        shoutouts
                    )
                }",
                fontFamily = lato,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                color = GrayOne
            )
        }

        AsyncImage(
            model = article.imageURL, modifier = Modifier
                .height(100.dp)
                .width(100.dp), contentDescription = null, contentScale = ContentScale.Crop
        )
    }
}
