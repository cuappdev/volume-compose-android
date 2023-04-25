package com.cornellappdev.android.volume.ui.components.general

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.cornellappdev.android.volume.R
import com.cornellappdev.android.volume.data.models.Flyer
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif

@Composable
fun BigFlyer(imgSize: Dp, flyer: Flyer) {
    val iconSize = if (imgSize > 256.dp) 24.dp else 16.dp

    Column (modifier = Modifier.width(imgSize)) {
        // Image and tag
        Box {

            Box(
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp)
                    .zIndex(1F)
            ) {
                Box(
                    modifier = Modifier
                        .background(VolumeOrange, shape = RoundedCornerShape(8.dp))
                        .zIndex(1F)
                ) {
                    // Tag
                    Text(
                        text = flyer.organization.categorySlug,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 2.dp, horizontal = 8.dp)
                    )
                }
            }
            // Image
            AsyncImage(
                model = flyer.imageURL,
                contentDescription = null,
                modifier = Modifier
                    .size(size = imgSize)
                    .zIndex(0F)
                    .shimmerEffect(),
            )
        }

        // Organization and icon row
        OrganizationAndIconsRow(organizationName = flyer.organization.name, inBigFlyer = true, iconSize = iconSize)

        // Event title text
        Text(
            text = flyer.title,
            fontFamily = notoserif,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        IconTextRow(text = flyer.date, iconId = R.drawable.ic_calendar)
        Spacer(modifier = Modifier.height(5.dp))
        IconTextRow(text = flyer.location, iconId = R.drawable.ic_location_pin)
    }
}

@Composable
fun SmallFlyer(inUpcoming: Boolean, flyer: Flyer) {
    var modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp, end = 16.dp)
    if (inUpcoming) {
        modifier = Modifier
            .width(352.dp)
            .padding(bottom = 16.dp, end = 16.dp)
    }
    Row (modifier = modifier) {
        AsyncImage(
            model = flyer.imageURL,
            contentDescription = null,
            modifier = if (inUpcoming) Modifier.shimmerEffect() else Modifier.size(width = 130.dp, height = 130.dp).shimmerEffect()
        )
        Column (modifier = Modifier.padding(start = 8.dp)) {
            OrganizationAndIconsRow(organizationName = flyer.organization.name, iconSize = 20.dp)
            Text(
                text = flyer.title,
                fontFamily = notoserif,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(
                modifier = Modifier
                    .height(8.dp)
                    .fillMaxWidth()
            )
            IconTextRow(text = flyer.date, iconId = R.drawable.ic_calendar)
            Spacer(
                modifier = Modifier
                    .height(8.dp)
                    .fillMaxWidth()
            )
            IconTextRow(text = flyer.location, iconId = R.drawable.ic_location_pin,
                modifier = if (inUpcoming) Modifier.offset(y = (-2).dp) else Modifier)
            if (!inUpcoming) {
                // Show the tag:
                Spacer(modifier = Modifier
                    .height(8.dp)
                    .fillMaxWidth())
                Box(modifier = Modifier.drawWithContent {
                    drawContent()
                    drawRoundRect(
                        color = VolumeOrange,
                        style = Stroke(width = 1.5.dp.toPx()),
                        cornerRadius = CornerRadius(
                            x = 5.dp.toPx(),
                            y = 5.dp.toPx()
                        ),
                    )
                }) {
                    Text(
                        text = flyer.organization.categorySlug,
                        color = VolumeOrange,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
fun OrganizationAndIconsRow(organizationName: String, inBigFlyer: Boolean = false, iconSize: Dp) {
    if (inBigFlyer) {
        Spacer(modifier = Modifier
            .height(8.dp)
            .fillMaxWidth())
    }
    Row(modifier = Modifier
        .fillMaxWidth()) {
        Text(
            text = organizationName,
            fontFamily = notoserif,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.weight(1F))
        Image(
            painter = painterResource(id = R.drawable.ic_bookmark_orange_empty),
            contentDescription = null,
            modifier = Modifier.size(iconSize),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_share_black),
            contentDescription = null,
            modifier = Modifier.size(iconSize),
        )
    }
}

@Composable
fun IconTextRow(text: String, iconId: Int, modifier: Modifier = Modifier) {
    Row {
        Icon(painter = painterResource(id = iconId), contentDescription = null)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, fontFamily = lato, fontWeight = FontWeight.Normal, fontSize = 12.sp, modifier = modifier)

    }
}