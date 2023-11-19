package com.cornellappdev.android.volume.ui.components.general

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cornellappdev.android.volume.ui.theme.GrayFour
import com.cornellappdev.android.volume.ui.theme.GrayOne
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif

/**
 * Creates a Horizontal Partner Row for the Partner passed in.
 *
 * The callback returns the partner back when the follow button is
 * clicked, and returns true when the partner is followed.
 *
 * @param publication
 * @param followButtonClicked
 */
@Composable
fun CreatePartnerRow(
    profileImageURL: String?,
    name: String,
    slug: String,
    bio: String?,
    mostRecentArticleTitle: String? = null,
    onPartnerClick: (slug: String) -> Unit,
    isFollowed: Boolean,
    followButtonClicked: (partnerSlug: String, Boolean) -> Unit,
) {
    val hasBeenClicked = rememberSaveable { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth(),
    ) {
        AsyncImage(
            model = profileImageURL,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.Gray)
                .clickable { onPartnerClick(slug) },
            contentDescription = null
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.height(IntrinsicSize.Min)
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .weight(1f)
                        .clickable { onPartnerClick(slug) },
                    text = name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = notoserif,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )

                OutlinedButton(
                    modifier = Modifier.size(33.dp),
                    // Passes true to callback if followed, false if unfollowed.
                    onClick = {
                        hasBeenClicked.value = !hasBeenClicked.value
                        followButtonClicked(slug, isFollowed)
                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = if (hasBeenClicked.value) VolumeOrange else Color.White),
                    border = if (hasBeenClicked.value) null else BorderStroke(2.dp, Color.Black)
                ) {
                    Crossfade(targetState = hasBeenClicked.value, label = "") { hasBeenClicked ->
                        if (hasBeenClicked) {
                            Icon(
                                Icons.Default.Done,
                                contentDescription = "Followed",
                                tint = Color.White
                            )
                        } else {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Follow",
                                tint = Color.Black
                            )
                        }
                    }
                }

            }
            if (!bio.isNullOrBlank()) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .padding(bottom = 2.dp)
                ) {

                    Text(
                        modifier = Modifier.padding(end = 20.dp),
                        text = bio,
                        color = GrayOne,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = lato,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.fillMaxHeight())
                }
            }
            mostRecentArticleTitle?.let {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.height(IntrinsicSize.Min)
                ) {
                    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                        Divider(
                            color = GrayFour,
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                        )

                        Text(
                            modifier = Modifier.padding(start = 8.dp, end = 20.dp),
                            text = "\"$it\"",
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = lato,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.fillMaxHeight())
                }
            }
        }
    }
}

@Composable
fun CreatePartnerColumn(
    profileImageURL: String?,
    slug: String,
    title: String,
    onPartnerClick: (slug: String) -> Unit,
) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .width(100.dp)
            .clickable {
                onPartnerClick(slug)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = profileImageURL, modifier = Modifier
                .height(100.dp)
                .width(100.dp)
                .clip(CircleShape)
                .background(Color.Gray), contentDescription = null, contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier.padding(bottom = 2.dp, top = 2.dp),
            text = title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontFamily = notoserif,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
        )
    }
}


