package com.cornellappdev.android.volume.ui.components.general

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.cornellappdev.android.volume.R
import com.cornellappdev.android.volume.data.models.Flyer
import com.cornellappdev.android.volume.data.models.Organization
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun BigFlyer(imgSize: Dp, flyer: Flyer) {
    val iconSize = if (imgSize > 256.dp) 24.dp else 16.dp
    val imageURL = flyer.imageURL
    val context = LocalContext.current
    var imageBitmap by remember(imageURL) { mutableStateOf<Bitmap?>(null) }
    var averageColor by remember { mutableStateOf(Color.Gray) }
    val openLinkLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {}

    // Gets the average image color for background
    LaunchedEffect(key1 = flyer.imageURL) {
        imageBitmap = getBitmap(imageURL, context)
        averageColor = getAverageColor(imageBitmap!!).toComposeColor()
    }

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
                        text = flyer.organizations.formatTypes(),
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
                    .background(color = averageColor).clickable { val uri = Uri.parse(flyer.postURL)
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            openLinkLauncher.launch(intent)
                        } catch (ignored: ActivityNotFoundException) {} },
            )
        }

        // Organization and icon row
        OrganizationAndIconsRow(organizationName = flyer.organizations
            .joinToString(transform = {o -> o.name.replaceFirstChar { c -> c.uppercase() }},
                separator = ", "), inBigFlyer = true, iconSize = iconSize, url = flyer.postURL,
            context = LocalContext.current)

        // Event title text
        Text(
            text = flyer.title,
            fontFamily = notoserif,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        IconTextRow(text = formatDateString(flyer.startDate, flyer.endDate), iconId = R.drawable.ic_calendar)
        Spacer(modifier = Modifier.height(5.dp))
        IconTextRow(text = flyer.location, iconId = R.drawable.ic_location_pin)
    }
}

@Composable
fun SmallFlyer(inUpcoming: Boolean, flyer: Flyer) {
    val imageURL = flyer.imageURL
    val context = LocalContext.current
    val openLinkLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {}

    var imageBitmap by remember(imageURL) { mutableStateOf<Bitmap?>(null) }
    var averageColor by remember { mutableStateOf(Color.Gray) }

    // Gets the average image color for background
    LaunchedEffect(key1 = flyer.imageURL) {
        imageBitmap = getBitmap(imageURL, context)
        averageColor = getAverageColor(imageBitmap!!).toComposeColor()
    }

    var modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp, end = 16.dp)
    if (inUpcoming) {
        modifier = Modifier
            .width(352.dp)
            .padding(bottom = 16.dp, end = 16.dp)
    }
    Row (modifier = modifier) {
        // For some reason image clickability only works when it's in a box in this case
        // This is the cover image
        Box (modifier = Modifier.clickable {
            val uri = Uri.parse(flyer.postURL)
            try {
                val intent = Intent(Intent.ACTION_VIEW, uri)
                openLinkLauncher.launch(intent)
            } catch (ignored: ActivityNotFoundException) {}
        }) {
            AsyncImage(
                model = flyer.imageURL,
                contentDescription = null,
                modifier = if (inUpcoming) Modifier
                    .background(color = averageColor)
                    .size(123.dp) else Modifier
                    .size(width = 130.dp, height = 130.dp)
                    .background(color = averageColor)
            )
        }

        Column (modifier = Modifier.padding(start = 8.dp)) {
            OrganizationAndIconsRow(organizationName = flyer.organizations
                .toSet()
                .joinToString(transform = {o -> o.name}, separator = ", "), iconSize = 20.dp, url = flyer.postURL,
                context = LocalContext.current)
            // Flyer title
            Text(
                text = flyer.title,
                fontFamily = notoserif,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(
                modifier = Modifier
                    .height(4.dp)
                    .fillMaxWidth()
            )
            IconTextRow(text = formatDateString(flyer.startDate, flyer.endDate), iconId = R.drawable.ic_calendar)
            Spacer(
                modifier = Modifier
                    .height(4.dp)
                    .fillMaxWidth()
            )
            IconTextRow(text = flyer.location, iconId = R.drawable.ic_location_pin)
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
                        text = flyer.organizations.formatTypes(),
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

/**
 * Formats the types for multiple organizations so you can see them clearly in the tag.
 */
fun List<Organization>.formatTypes(): String {
    return  this.map { o -> o.type.replaceFirstChar { c -> c.uppercase() } }.toSet().joinToString(separator = ", ")
}


@Composable
fun OrganizationAndIconsRow(organizationName: String, inBigFlyer: Boolean = false, iconSize: Dp,
    url: String, context: Context) {
    if (inBigFlyer) {
        Spacer(modifier = Modifier
            .height(8.dp)
            .fillMaxWidth())
    }
    Row(modifier = Modifier
        .fillMaxWidth()) {
        // Org name
        Text(
            text = organizationName,
            fontFamily = notoserif,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.weight(1F))
        // Bookmark icon
        Image(
            painter = painterResource(id = R.drawable.ic_bookmark_orange_empty),
            contentDescription = null,
            modifier = Modifier.size(iconSize),
        )
        Spacer(modifier = Modifier.width(8.dp))
        // Share icon
        Icon(
            painter = painterResource(id = R.drawable.ic_share_black),
            contentDescription = null,
            modifier = Modifier.size(iconSize).clickable {
                 shareFlyer(context = context, url = url)
            },
        )
    }
}

@Composable
fun IconTextRow(text: String, iconId: Int, modifier: Modifier = Modifier) {
    Row (modifier = Modifier) {
        Icon(painter = painterResource(id = iconId), contentDescription = null)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, fontFamily = lato, fontWeight = FontWeight.Normal, fontSize = 12.sp, modifier = modifier)
    }
}

private suspend fun getBitmap(imageUrl: String, context: Context): Bitmap? {
    val loading = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .build()

    return when (val res = loading.execute(request)) {
        is SuccessResult -> {
            val bitDrawable = res.drawable
            (bitDrawable as BitmapDrawable).bitmap
        }
        else -> {
            null
        }
    }
}

/**
 * Gets the average color of an image in the form of an int. First scales the image down to
 * improve performance.
 */
private fun getAverageColor(immutableBitmap: Bitmap): Int {
    // Scale bitmap to make it go faster :)
    val bitmap = Bitmap.createScaledBitmap(immutableBitmap.copy(Bitmap.Config.RGBA_F16, true), 120 , 120, false)


    // Calculate the total sum of red, green, and blue values
    var totalRed = 0
    var totalGreen = 0
    var totalBlue = 0

    for (y in 0 until bitmap.height) {
        for (x in 0 until bitmap.width) {
            val color = bitmap.getPixel(x, y)
            totalRed += android.graphics.Color.red(color)
            totalGreen += android.graphics.Color.green(color)
            totalBlue += android.graphics.Color.blue(color)
        }
    }

    // Calculate the average red, green, and blue values
    val averageRed = totalRed / (bitmap.width * bitmap.height)
    val averageGreen = totalGreen / (bitmap.width * bitmap.height)
    val averageBlue = totalBlue / (bitmap.width * bitmap.height)

    // Return the average color in RGB format
    return android.graphics.Color.rgb(averageRed, averageGreen, averageBlue)
}

/**
 * Formats date string in the desired format for displaying.
 */
private fun formatDateString(startDate: String, endDate: String): String {
    val startDateTime = LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern("MMM d yy h:mm a"))
    val endDateTime = LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern("MMM d yy h:mm a"))

    val dayOfWeek = startDateTime.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)
    val month = startDateTime.month.getDisplayName(TextStyle.SHORT, Locale.US)
    val dayOfMonth = startDateTime.dayOfMonth

    val startTime = if (startDateTime.minute == 0) {
        startDateTime.format(DateTimeFormatter.ofPattern("ha"))
    } else {
        startDateTime.format(DateTimeFormatter.ofPattern("h:mm a"))
    }

    val endTime = if (endDateTime.minute == 0) {
        endDateTime.format(DateTimeFormatter.ofPattern("ha"))
    } else {
        endDateTime.format(DateTimeFormatter.ofPattern("h:mm a"))
    }

    return "$dayOfWeek, $month $dayOfMonth $startTime - $endTime"
}

/**
 * Opens the snackbar to share a Flyer, which shares its associated post URL.
 */
private fun shareFlyer(context: Context, url: String) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(
            Intent.EXTRA_TEXT,
            "Look at this event I found from Volume: $url"
        )
    }
    context.startActivity(Intent.createChooser(intent, "Share To:"))
}
