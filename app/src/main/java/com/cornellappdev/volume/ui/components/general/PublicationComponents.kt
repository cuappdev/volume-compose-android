package com.cornellappdev.volume.ui.components.general

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.cornellappdev.volume.data.models.Publication
import com.cornellappdev.volume.ui.theme.*

/**
 * Creates a Horizontal Publication Row for the Publication passed in.
 *
 * The callback returns the publication back when the follow button is
 * clicked, and returns true when the publication is followed.
 *
 * @param publication
 * @param followButtonClicked
 */
@Composable
fun CreateHorizontalPublicationRow(
    publication: Publication,
    followButtonClicked: (Publication, Boolean) -> Unit,
) {
    val hasBeenClicked = rememberSaveable { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth(),
    ) {
        AsyncImage(
            model = publication.profileImageURL,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(64.dp)
                .clip(CircleShape),
            contentDescription = null
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.height(IntrinsicSize.Min)
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .weight(1f),
                    text = publication.name,
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
                        followButtonClicked(publication, hasBeenClicked.value)
                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = if (hasBeenClicked.value) VolumeOrange else Color.White),
                    border = if (hasBeenClicked.value) null else BorderStroke(2.dp, Color.Black)
                ) {
                    Crossfade(targetState = hasBeenClicked.value) { hasBeenClicked ->
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

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .padding(bottom = 2.dp)
            ) {

            Text(
                    modifier = Modifier.padding(end = 20.dp),
                    text = publication.bio,
                    color = GrayOne,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = lato,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.fillMaxHeight())
            }

            publication.mostRecentArticle?.let {
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
                            text = "\"${it.title}\"",
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
fun CreateHorizontalPublicationRowFollowing(
    publication: Publication,
    followButtonClicked: (Publication, Boolean) -> Unit,
) {
    var hasBeenClicked = false
    Row(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth(),
    ) {
        AsyncImage(
            model = publication.profileImageURL,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(64.dp)
                .clip(CircleShape),
            contentDescription = null
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.height(IntrinsicSize.Min)
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .weight(1f),
                    text = publication.name,
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
                        hasBeenClicked = !hasBeenClicked
                        followButtonClicked(publication, hasBeenClicked)
                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = if (hasBeenClicked) VolumeOrange else Color.White),
                    border = if (hasBeenClicked) null else BorderStroke(2.dp, Color.Black)
                ) {
                    Crossfade(targetState = hasBeenClicked) { hasBeenClicked ->
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

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .padding(bottom = 2.dp)
            ) {

                Text(
                    modifier = Modifier.padding(end = 20.dp),
                    text = publication.bio,
                    color = GrayOne,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = lato,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.fillMaxHeight())
            }

            publication.mostRecentArticle?.let {
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
                            text = "\"${it.title}\"",
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
fun CreateFollowPublicationRow (
    publication: Publication,
    onPublicationClick: (Publication) -> Unit
){
    val title= publication.name

    Column(modifier = Modifier
        .wrapContentHeight()
        .width(100.dp)
        .clickable {
            onPublicationClick(publication)
        }){

        AsyncImage(
            model = publication.profileImageURL, modifier = Modifier
                .height(100.dp)
                .width(100.dp)
                .clip(CircleShape), contentDescription = null, contentScale = ContentScale.Crop
        )

        Text(
            modifier = Modifier.padding(bottom = 2.dp, top = 2.dp),
            text = title,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            fontFamily = notoserif,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}


