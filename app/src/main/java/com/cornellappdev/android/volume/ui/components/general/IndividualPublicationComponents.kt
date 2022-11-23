package com.cornellappdev.android.volume.ui.components.general

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cornellappdev.android.volume.R
import com.cornellappdev.android.volume.data.models.Publication
import com.cornellappdev.android.volume.data.models.Social.Companion.formattedSocialNameMap
import com.cornellappdev.android.volume.data.models.Social.Companion.socialLogoMap
import com.cornellappdev.android.volume.ui.theme.*

@Composable
fun CreateIndividualPublicationHeading(
    publication: Publication,
    followButton: Boolean,
    followButtonClicked: (Boolean) -> Unit,
) {
    val hasBeenClicked = rememberSaveable { mutableStateOf(followButton) }
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
                alignment = Alignment.TopStart,
                modifier = Modifier
                    .padding(start = 8.dp, top = 130.dp)
                    .size(64.dp)
                    .clip(CircleShape)
                    .shadow(4.dp, CircleShape),
                contentDescription = null
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 20.dp),
                    text = publication.name,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = notoserif,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )

                Button(
                    modifier = Modifier
                        .height(33.dp),
                    onClick = {
                        hasBeenClicked.value = !hasBeenClicked.value
                        followButtonClicked(hasBeenClicked.value)
                    },
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = if (hasBeenClicked.value) VolumeOrange else GrayThree),
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Follow",
                                    tint = VolumeOrange
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text(
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
                text = "${publication.numArticles.toInt()} articles · ${publication.shoutouts.toInt()} shoutouts",
                fontFamily = lato,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                color = GrayOne
            )

            Text(
                modifier = Modifier.padding(top = 2.dp),
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
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                for (social in publication.socials) {
                    val socialName =
                        formattedSocialNameMap.getOrDefault(social.social, social.social)

                    Row {
                        // Make sure that the drawable is in the socialLogoMap or the painter is null
                        HyperlinkText(
                            displayText = socialName,
                            uri = social.url,
                            style = TextStyle(fontFamily = lato, color = VolumeOrange),
                            painter = socialLogoMap[socialName]?.let { painterResource(it) },
                        )
                    }
                }

                val websiteURL =
                    publication.websiteURL.removePrefix("https://").removePrefix("http://")
                        .removePrefix("www.").removeSuffix("/")

                HyperlinkText(
                    displayText = websiteURL,
                    uri = publication.websiteURL,
                    style = TextStyle(
                        fontFamily = lato,
                        color = VolumeOrange,
                        textDecoration = TextDecoration.Underline
                    ),
                    painter = painterResource(R.drawable.ic_link),
                )
            }
        }
    }
}

@Composable
fun HyperlinkText(
    displayText: String,
    uri: String,
    style: TextStyle,
    painter: Painter?
) {
    val uriHandler = LocalUriHandler.current

    TextButton(
        contentPadding = PaddingValues(0.dp),
        onClick = {
            uriHandler.openUri(uri)
        }
    ) {
        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = "Icon",
            )

            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        }

        Text(
            text = displayText,
            maxLines = 1,
            style = style,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
