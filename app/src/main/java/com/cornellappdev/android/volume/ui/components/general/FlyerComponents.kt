package com.cornellappdev.android.volume.ui.components.general

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.cornellappdev.android.volume.R
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif

@Preview
@Composable
fun BigFlyer() {
    Column (modifier = Modifier.width(256.dp)) {
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
                    Text(
                        text = "Dance",
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 2.dp, horizontal = 8.dp)
                    )
                }
            }
            AsyncImage(
                model = "https://images.squarespace-cdn.com/content/v1/60eb5b94ffc5d0139c894a84/1651730346785-XR81CQIRLK0KWIWL9EJV/Extra+Logos.png?format=1000w",
                contentDescription = null,
                modifier = Modifier
                    .size(width = 256.dp, height = 256.dp)
                    .zIndex(0F)
            )
        }

        // Organization and icon row
        OrganizationAndIconsRow(organizationName = "Break Free")

        // Event title text
        Text(
            text = "New Destinations",
            fontFamily = notoserif,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        IconTextRow(text = "Fri, April 14th  4PM - 6PM", iconId = R.drawable.ic_calendar)
        Spacer(modifier = Modifier.height(5.dp))
        IconTextRow(text = "Bailey Hall", iconId = R.drawable.ic_location_pin)
    }
}

@Composable
fun SmallFlyer(inUpcoming: Boolean) {
    var modifier = Modifier.size(width = 361.dp, height = 123.dp)
    if (inUpcoming) {
        modifier = Modifier.size(height = 92.dp, width = 352.dp)
    }
    Row (modifier = modifier.padding(bottom = 16.dp, end = 16.dp)) {
        AsyncImage(
            model = "https://images.squarespace-cdn.com/content/v1/60eb5b94ffc5d0139c894a84/1651730346785-XR81CQIRLK0KWIWL9EJV/Extra+Logos.png?format=1000w",
            contentDescription = null,
            modifier = if (inUpcoming) Modifier.size(width = 123.dp, height = 123.dp) else Modifier
        )
        Column (modifier = Modifier.padding(start = 8.dp))  {
            OrganizationAndIconsRow(organizationName = "Break Free")
            Text(
                text = "New Destinations",
                fontFamily = notoserif,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            IconTextRow(text = "Sat, March 25th  4PM - 5:30PM", iconId = R.drawable.ic_calendar)
            IconTextRow(text = "Physical Sciences Building", iconId = R.drawable.ic_location_pin)
        }
    }
}

@Composable
fun OrganizationAndIconsRow(organizationName: String) {
    Row(modifier = Modifier.padding(top = 8.dp).fillMaxWidth()) {
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
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_share_black),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun IconTextRow(text: String, iconId: Int) {
    Row {
        Icon(painter = painterResource(id = iconId), contentDescription = null)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, fontFamily = lato, fontWeight = FontWeight.Normal, fontSize = 12.sp)
    }
}