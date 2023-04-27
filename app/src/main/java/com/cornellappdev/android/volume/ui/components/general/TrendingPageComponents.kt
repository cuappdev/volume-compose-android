package com.cornellappdev.android.volume.ui.components.general

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color.blue
import android.graphics.Color.green
import android.graphics.Color.red
import android.graphics.Color.rgb
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.cornellappdev.android.volume.analytics.NavigationSource
import com.cornellappdev.android.volume.data.models.Article
import com.cornellappdev.android.volume.ui.theme.notoserif

private const val TAG = "TrendingPageComponents"

@Composable
fun MainArticleComponent(article: Article, onArticleClick:
    (Article, NavigationSource) -> Unit) {
    val title = article.title
    val publisher = article.publication.name
    val time = article.getTimeSinceArticlePublished()
    val imageURL = article.imageURL
    var imageBitmap by remember(imageURL) { mutableStateOf<Bitmap?>(null) }
    var bottomAverageColor by remember { mutableStateOf(Color.Transparent) }
    // Let the default linear gradient just be a transparent gradient.
    val defaultGradient = LinearGradientShader(
        colors = listOf(
            Color.Transparent,
            Color.Transparent),
        from = Offset.Zero,
        to = Offset.Zero,
        tileMode = TileMode.Clamp
    )
    var linearGradient by remember {
        mutableStateOf<ShaderBrush?>(object : ShaderBrush() {
            override fun createShader(size: Size): androidx.compose.ui.graphics.Shader {
                return defaultGradient
            }
        })
    }

    val config = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = config.screenWidthDp.dp

    // Launch a coroutine scope to asynchronously fetch image bitmap and update gradient:
    LaunchedEffect (context) {
        imageBitmap = getBitmap(imageURL, context)
        bottomAverageColor = getAverageBottomColor(imageBitmap!!).toComposeColor()
        linearGradient =
            object : ShaderBrush() {
                override fun createShader(size: Size): Shader {
                    when (imageBitmap) {
                        null -> {
                            return defaultGradient
                        }
                        is Bitmap -> {
                            return LinearGradientShader(
                                    colors = listOf(
                                        bottomAverageColor,
                                        Color.Transparent),
                            from = Offset(0f, size.height),
                            to = Offset.Zero,
                            tileMode = TileMode.Clamp
                            )
                        } else -> {
                        return defaultGradient
                    }
                }
            }
        }
    }

    // Actual main article component content
    Column (modifier = Modifier.clickable {
        onArticleClick(article, NavigationSource.TRENDING_ARTICLES)
    })  {
        Box (modifier = Modifier.size(screenWidth)) {
        AsyncImage(model = imageURL,
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .size(
                    width = screenWidth,
                    height = screenWidth
                )
                .shimmerEffect())
        // Apply gradient to the bottom of the image
        Box  (modifier = Modifier
            .requiredWidth(screenWidth)
            .requiredHeight(64.dp)
            .background(linearGradient as Brush)
            .align(Alignment.BottomCenter)
        )
    }
        // Put text with background below the image
        Column (modifier = Modifier
            .background(bottomAverageColor)
            .fillMaxWidth()) {
            // Publisher text
            Text(text = publisher, color = Color.White, modifier = Modifier
                .padding(horizontal = 16.dp),
                fontFamily = notoserif,
                fontSize = 10.sp,
            )
            // Title text
            Text(text = title, color = Color.White, modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp),
                fontFamily = notoserif,
                fontSize = 20.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            // Time text
            Text(text = time, color = Color.White, modifier = Modifier
                .padding(start = 16.dp, bottom = 16.dp),
                fontFamily = notoserif,
                fontSize = 10.sp,
            )
        }
    }
}


/**
 * Given an image URL, asynchronously returns the bitmap of the image contained at the URL.
 */
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
 * Gets the average bottom 20% of color for an image, given an image bitmap.
 */
private fun getAverageBottomColor(immutableBitmap: Bitmap): Int {
    // Calculate the height of the bottom 20% of the image
    val bitmap = immutableBitmap.copy(Bitmap.Config.RGBA_F16, true)

    val bottom20PercentHeight = (bitmap.height * 0.2).toInt()

    // Extract the bottom 20% of the image
    val bottom20Percent = Bitmap.createBitmap(bitmap, 0, bitmap.height - bottom20PercentHeight, bitmap.width, bottom20PercentHeight)

    // Calculate the total sum of red, green, and blue values
    var totalRed = 0
    var totalGreen = 0
    var totalBlue = 0

    for (y in 0 until bottom20Percent.height) {
        for (x in 0 until bottom20Percent.width) {
            val color = bottom20Percent.getPixel(x, y)
            totalRed += red(color)
            totalGreen += green(color)
            totalBlue += blue(color)
        }
    }

    // Calculate the average red, green, and blue values
    val averageRed = totalRed / (bottom20Percent.width * bottom20Percent.height)
    val averageGreen = totalGreen / (bottom20Percent.width * bottom20Percent.height)
    val averageBlue = totalBlue / (bottom20Percent.width * bottom20Percent.height)

    // Return the average color in RGB format
    return rgb(averageRed, averageGreen, averageBlue)
}