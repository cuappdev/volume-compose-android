package com.cornellappdev.volume.ui.components.general

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cornellappdev.volume.R
import com.cornellappdev.volume.data.models.Publication
import com.cornellappdev.volume.ui.theme.*

@Composable
fun CreateIndividualPublicationHeading(
    publication: Publication,
    followButtonClicked: (Boolean) -> Unit,
) {
    val hasBeenClicked = rememberSaveable { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Box {

            AsyncImage(
                model = publication.backgroundImageURL,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth(),
                contentDescription = null
            )

            AsyncImage(
                model = publication.profileImageURL,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = (8.dp), top = 130.dp)
                    .size(64.dp)
                    .clip(CircleShape),
                contentDescription = null
            )
        }
        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 12.dp, top = 2.dp)
                    .wrapContentHeight()
                    .width(230.dp),
                text = publication.name,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                fontFamily = notoserif,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp
            )
            OutlinedButton(
                modifier = Modifier
                    .padding(start = 250.dp)
                    .size(width = 120.dp, height = 33.dp),
                onClick = {
                    hasBeenClicked.value = !hasBeenClicked.value
                    followButtonClicked(hasBeenClicked.value)
                },
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = if (hasBeenClicked.value) VolumeOrange else GrayThree),
                border = null
            ) {
                Crossfade(targetState = hasBeenClicked.value) { hasBeenClicked ->
                    if (hasBeenClicked) {
                        Text(
                            text = "Following",
                            fontFamily = lato,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = GrayThree
                        )
                    } else {
                        Row(horizontalArrangement = Arrangement.Center) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Follow",
                                modifier = Modifier.scale(.8f),
                                tint = VolumeOrange
                            )
                            Text(
                                modifier = Modifier.padding(top = 2.dp, start = 6.dp),
                                text = "Follow",
                                textAlign = TextAlign.Center,
                                fontFamily = lato,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = VolumeOrange
                            )
                        }

                    }
                }
            }

        }
        Text(
            modifier = Modifier.padding(start = 12.dp),
            text = "${publication.numArticles.toInt()} articles · ${publication.shoutouts.toInt()} shoutouts",
            fontFamily = lato,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            color = GrayOne
        )

        Text(
            modifier = Modifier.padding(start = 12.dp, top = 2.dp, end = 20.dp),
            text = publication.bio,
            maxLines = 6,
            overflow = TextOverflow.Ellipsis,
            fontFamily = lato,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 12.dp, end = 12.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            for (social in publication.socials) {
                if (social.social == "instagram") {
                    Image(
                        modifier = Modifier
                            .scale(1.3f),
                        painter = painterResource(R.drawable.ic_instagram),
                        contentDescription = null,
                    )
                    HyperlinkText(
                        fullText = "Instagram",
                        modifier = Modifier.padding(start = 10.dp),
                        hyperLinks = Pair("Instagram", social.url),
                        style = TextStyle(fontFamily = lato, color = VolumeOrange)
                    )
                }
                if (social.social == "facebook") {
                    Image(
                        modifier = Modifier
                            .scale(1.3f),
                        painter = painterResource(R.drawable.ic_facebook),
                        contentDescription = null,
                    )
                    HyperlinkText(
                        fullText = "Facebook",
                        modifier = Modifier.padding(start = 10.dp),
                        hyperLinks = Pair("Facebook", social.url),
                        style = TextStyle(fontFamily = lato, color = VolumeOrange)
                    )
                }
            }
            Image(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .scale(1.3f),
                painter = painterResource(R.drawable.ic_link),
                contentDescription = null,
            )
            HyperlinkText(
                fullText = publication.websiteURL,
                modifier = Modifier.padding(start = 10.dp),
                hyperLinks = Pair(publication.name, publication.websiteURL),
                style = TextStyle(
                    fontFamily = lato,
                    color = VolumeOrange,
                    textDecoration = TextDecoration.Underline
                )
            )
        }

    }
}

@Composable
fun HyperlinkText(
    modifier: Modifier = Modifier,
    fullText: String,
    hyperLinks: Pair<String, String>,
    style: TextStyle
) {
    val annotatedString = buildAnnotatedString {
        append(fullText)
        val startIndex = fullText.indexOf(hyperLinks.first)
        val endIndex = startIndex + hyperLinks.first.length
        addStyle(
            style = SpanStyle(
                color = VolumeOrange,
                fontSize = 12.sp,
            ),
            start = startIndex,
            end = endIndex
        )
        addStringAnnotation(
            tag = "URL",
            annotation = hyperLinks.second,
            start = startIndex,
            end = endIndex
        )

        addStyle(
            style = SpanStyle(
                fontSize = 12.sp
            ),
            start = 0,
            end = fullText.length
        )
    }

    val uriHandler = LocalUriHandler.current

    ClickableText(
        modifier = modifier,
        text = annotatedString,
        maxLines = 1,
        style = style,
        overflow = TextOverflow.Ellipsis,
        onClick = {
            annotatedString
                .getStringAnnotations("URL", it, it)
                .firstOrNull()?.let { stringAnnotation ->
                    uriHandler.openUri(stringAnnotation.item)
                }
        },
    )
}
