package com.cornellappdev.android.volume.ui.components.general

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.cornellappdev.android.volume.data.models.Social
import com.cornellappdev.android.volume.ui.theme.GrayOne
import com.cornellappdev.android.volume.ui.theme.GrayThree
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif

@Composable
fun CreateIndividualPartnerHeading(
    followButton: Boolean,
    followButtonClicked: (Boolean) -> Unit,
    backgroundImageUrl: String?,
    profileImageURL: String?,
    partnerName: String,
    statsText: String,
    bio: String?,
    socials: List<Social>?,
    websiteURL: String,
) {
    var hasBeenClicked by remember { mutableStateOf(followButton) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Box {
            if (backgroundImageUrl.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Gray)
                )
            } else {
                AsyncImage(
                    model = backgroundImageUrl,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentDescription = null
                )
            }
            if (profileImageURL.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp, top = 130.dp)
                        .size(64.dp)
                        .clip(CircleShape)
                        .shadow(4.dp, CircleShape)
                        .background(Color.Gray)
                )
            } else {
                AsyncImage(
                    model = profileImageURL,
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
                    text = partnerName,
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
                        hasBeenClicked = !hasBeenClicked
                        followButtonClicked(hasBeenClicked)
                    },
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = if (hasBeenClicked) VolumeOrange else GrayThree),
                ) {
                    Crossfade(targetState = hasBeenClicked, label = "") { hasBeenClicked ->
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
                text = statsText,
                fontFamily = lato,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                color = GrayOne
            )
            bio?.let {
                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    text = it,
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = lato,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                socials?.forEach { social ->
                    val socialName =
                        Social.formattedSocialNameMap.getOrDefault(social.social, social.social)

                    Row {
                        // Make sure that the drawable is in the socialLogoMap or the painter is null
                        HyperlinkText(
                            displayText = socialName,
                            uri = social.url,
                            style = TextStyle(fontFamily = lato, color = VolumeOrange),
                            painter = Social.socialLogoMap[socialName]?.let { painterResource(it) },
                        )
                    }
                }

                val formattedWebsiteURL =
                    websiteURL.removePrefix("https://").removePrefix("http://")
                        .removePrefix("www.").removeSuffix("/")

                HyperlinkText(
                    displayText = formattedWebsiteURL,
                    uri = websiteURL,
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

