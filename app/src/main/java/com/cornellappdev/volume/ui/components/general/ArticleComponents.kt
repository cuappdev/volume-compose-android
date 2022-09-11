package com.cornellappdev.volume.ui.components.general

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
import com.cornellappdev.volume.ui.theme.VolumeOrange
import com.cornellappdev.volume.ui.theme.lato
import com.cornellappdev.volume.ui.theme.notoserif

/**
 *
 * Creates a Horizontal Article Row for the article passed in.
 *
 * @param article
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreateHorizontalArticleRow(
    article: Article,
    isABookmarkedArticle: Boolean = false,
    onClick: (Article) -> Unit
) {
    val timeSincePublished = article.getTimeSinceArticlePublished()
    val shoutouts = article.shoutouts.toInt()
    Row(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
            .clickable {
                onClick(article)
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .height(100.dp)
                .weight(1f)
                .padding(end = 20.dp)
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

            Spacer(Modifier.weight(1f, true))

            Row {
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
                if (isABookmarkedArticle) {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = null,
                        tint = VolumeOrange,
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .size(12.dp)
                            .align(Alignment.Bottom)
                    )
                }
            }
        }

        AsyncImage(
            model = article.imageURL, modifier = Modifier
                .height(100.dp)
                .width(100.dp), contentDescription = null, contentScale = ContentScale.Crop
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreateBigReadRow(article: Article, onClick: (Article) -> Unit) {
    val timeSincePublished = article.getTimeSinceArticlePublished()
    val shoutouts = article.shoutouts.toInt()

    Column(modifier = Modifier
        .wrapContentHeight()
        .width(180.dp)
        .clickable {
            onClick(article)
        }) {
        AsyncImage(
            model = article.imageURL, modifier = Modifier
                .height(180.dp)
                .width(180.dp), contentDescription = null, contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier.padding(top = 16.dp, bottom = 2.dp),
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
        Spacer(modifier = Modifier.height(13.dp))
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
}
