package com.cornellappdev.volume.ui.components.general

import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.MagnifierStyle.Companion.Default
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.size.Dimension
import com.cornellappdev.volume.R
import com.cornellappdev.volume.data.models.Publication
import com.cornellappdev.volume.ui.theme.*
import com.cornellappdev.volume.ui.viewmodels.PublicationsViewModel
import org.intellij.lang.annotations.JdkConstants

@Composable
fun createIndividualPublicationHeading(
    publication: Publication,
    followButtonClicked: (Boolean) -> Unit,
) {
    val hasBeenClicked = rememberSaveable { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Box() {

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
        Box(modifier = Modifier.padding(top = 10.dp).fillMaxWidth()) {
            Text(
                modifier = Modifier.padding(start = 12.dp, top = 2.dp).wrapContentHeight().width(230.dp),
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
                colors = ButtonDefaults.buttonColors(backgroundColor = if (hasBeenClicked.value) VolumeOrange else GrayFour),
                border = null
            ) {
                Crossfade(targetState = hasBeenClicked.value) { hasBeenClicked ->
                    if (hasBeenClicked) {
                        Text(
                            text = "Following",
                            fontFamily = lato,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = Color.White
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
        Row(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                modifier = Modifier.padding(),
                text = "${publication.numArticles.toInt()} ${"articles · "}",
                fontFamily = lato,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                color = GrayOne
            )
            Text(
                modifier = Modifier.padding(),
                text = "${publication.shoutouts.toInt()} ${"shoutouts"}",
                fontFamily = lato,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                color = GrayOne
            )
        }
        Text(
            modifier = Modifier.padding(start = 12.dp, top = 2.dp, end = 20.dp),
            text = publication.bio,
            maxLines = 6,
            overflow = TextOverflow.Ellipsis,
            fontFamily = notoserif,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start=12.dp, end=12.dp), horizontalArrangement = Arrangement.Center
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
                        hyperLinks = mutableMapOf(
                            "Instagram" to social.url
                        ),
                        textStyle = TextStyle(
                            textAlign = TextAlign.Center,
                            color = VolumeOrange
                        ),
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
                        hyperLinks = mutableMapOf(
                            "Facebook" to social.url
                        ),
                        textStyle = TextStyle(
                            textAlign = TextAlign.Center,
                            color = VolumeOrange
                        ),
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
                fullText = publication.name,
                modifier = Modifier.padding(start = 10.dp),
                hyperLinks = mutableMapOf(
                    publication.name to publication.websiteURL
                ),
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    color = VolumeOrange
                ),
            )
        }

    }
}

@Composable
fun HyperlinkText(
    modifier: Modifier = Modifier,
    fullText: String,
    hyperLinks: Map<String, String>,
    textStyle: TextStyle = TextStyle.Default,
) {
    val annotatedString = buildAnnotatedString {
        append(fullText)
        for ((key, value) in hyperLinks) {
            val startIndex = fullText.indexOf(key)
            val endIndex = startIndex + key.length
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
                annotation = value,
                start = startIndex,
                end = endIndex
            )
        }
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
        style = TextStyle(fontFamily = lato),
        maxLines = 1,
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
