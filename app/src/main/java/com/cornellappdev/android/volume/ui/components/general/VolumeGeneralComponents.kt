package com.cornellappdev.android.volume.ui.components.general

import android.view.KeyEvent
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.volume.R
import com.cornellappdev.android.volume.ui.theme.GrayTwo
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif


// General components that have been abstracted for use throughout the Volume app
@Composable
fun VolumeLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.volume_title),
        contentDescription = null,
        modifier = modifier
            .scale(0.8f)
    )
}

@Composable
fun VolumeHeaderText(text: String, underline: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = text,
            fontFamily = notoserif,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
        )
        Image(
            painter = painterResource(underline),
            contentDescription = null,
            modifier = Modifier
                .offset(y = (-5).dp)
                .padding(start = 2.dp)
                .scale(1.05F)
        )
    }
}

// Custom modifiers for use throughout the app:
/**
 * Modifier extension function to provide a "shimmer effect"
 * Applies an animated gray background to any component with shimmer animation via gradients.
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition()
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000)
        )
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFCACACA),
                Color(0xFFA0A0A0),
                Color(0xFFCACACA),
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    ).onGloballyPositioned {
        size = it.size
    }
}

@Composable
fun VolumeLinearProgressBar(progress: Float, modifier: Modifier = Modifier.height(15.dp)) {
    Column(modifier = Modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            modifier = modifier,
            backgroundColor = Color.LightGray,
            color = VolumeOrange,
            progress = progress
        )
    }
}

@Composable
fun OldNothingToShowMessage(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_volume_bars_orange_large),
            contentDescription = null
        )

        Text(
            text = "Nothing to see here!",
            fontFamily = notoserif,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = message,
            fontFamily = lato,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun NothingToShowMessage(
    title: String,
    message: String,
    showImage: Boolean = false,
    imgId: Int = R.drawable.ic_volume_bars_orange_large,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .width(250.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showImage) {
                Image(
                    painter = painterResource(id = imgId),
                    contentDescription = null,
                    modifier = Modifier
                        .width(21.dp)
                        .height(28.dp),
                )
            }
            Text(
                text = title,
                fontFamily = notoserif,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = if (showImage) 6.dp else 16.dp)
            )
            Text(
                text = message,
                fontFamily = lato,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }

}

@Composable
fun VolumePeriod(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.ic_period),
        contentDescription = null,
        modifier = modifier
            .scale(1.05F)
    )
}

@Composable
fun ErrorState(modifier: Modifier = Modifier) {

    NothingToShowMessage(
        title = "No Connection",
        message = "Please try again later",
        showImage = true,
        imgId = R.drawable.ic_nowifi,
        modifier = modifier
    )
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    /*
    On enter pressed is currently unused, but I think it could be useful to have considering how
    this component could be used in the future, so I would like to keep it for now.
    If this functionality does end up being used, feel free to remove this comment :)
     */
    onEnterPressed: () -> Unit = {},
    onClick: () -> Unit = {},
    autoFocus: Boolean = false,
) {
    val source = remember { MutableInteractionSource() }
    val focusRequest = remember { FocusRequester() }
    if (autoFocus) {
        LaunchedEffect(key1 = "focus") {
            focusRequest.requestFocus()
        }
    }

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, shape = RoundedCornerShape(10.dp))
            .onKeyEvent {
                if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                    onEnterPressed()
                    return@onKeyEvent true
                }
                false
            }
            .focusRequester(focusRequest),
        leadingIcon = {
            Image(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = null
            )
        },
        placeholder = {
            Text(text = "Search", color = GrayTwo, fontSize = 16.sp, fontFamily = lato)
        },
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        interactionSource = source,
        singleLine = true,
    )
    if (source.collectIsPressedAsState().value)
        onClick()
}

fun Int.toComposeColor(): Color {
    val alpha = (this shr 24 and 0xFF) / 255f
    val red = (this shr 16 and 0xFF) / 255f
    val green = (this shr 8 and 0xFF) / 255f
    val blue = (this and 0xFF) / 255f
    return Color(red, green, blue, alpha)
}

