package com.cornellappdev.volume.ui.components.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.volume.R
import com.cornellappdev.volume.ui.theme.VolumeOrange
import com.cornellappdev.volume.ui.theme.lato
import com.cornellappdev.volume.ui.theme.notoserif

@Composable
fun FirstPage(onProceedClicked: () -> Unit) {
    val state = remember {
        MutableTransitionState(false).apply {
            // Start the animation immediately.
            targetState = true
        }
    }

    AnimatedVisibility(
        visibleState = state,
        enter = fadeIn(
            initialAlpha = 0f,
            animationSpec = tween(durationMillis = 2000)
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = 2000)
        )
    ) {
        Column(
            modifier = Modifier.wrapContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.volume_title),
                contentDescription = null
            )

            Text(
                modifier = Modifier.padding(top = 16.dp, bottom = 30.dp),
                text = "Welcome to Volume",
                maxLines = 1,
                fontFamily = notoserif,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp
            )

            BoxWithConstraints {
                Canvas(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(bottom = 24.dp)
                        .width(maxWidth / 2),
                ) {
                    drawLine(
                        color = Color(0xFFEEEEEE),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 2f
                    )
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 36.dp),
                verticalArrangement = Arrangement.spacedBy(50.dp)
            ) {
                Row {
                    Image(
                        modifier = Modifier.size(40.dp),
                        painter = painterResource(R.drawable.ic_publications_icon_selected),
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier.padding(start = 28.dp),
                        fontSize = 16.sp,
                        fontFamily = notoserif,
                        fontWeight = FontWeight.Medium,
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Stay updated")
                            }
                            append(" with Cornell student publications, all in once place")
                        }
                    )
                }

                Row {
                    Image(
                        modifier = Modifier.size(40.dp),
                        painter = painterResource(R.drawable.ic_volume_bars_orange),
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier.padding(start = 28.dp),
                        fontSize = 16.sp,
                        fontFamily = notoserif,
                        fontWeight = FontWeight.Medium,
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Read articles")
                            }
                            append(" trending in the Cornell community and from publications you follow")
                        }
                    )
                }

                Row {
                    Image(
                        modifier = Modifier.size(40.dp),
                        painter = painterResource(R.drawable.ic_shoutout),
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier.padding(start = 28.dp),
                        fontSize = 16.sp,
                        fontFamily = notoserif,
                        fontWeight = FontWeight.Medium,
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Give shout-outs")
                            }
                            append(" to support student content")
                        }
                    )
                }
            }

            OutlinedButton(
                modifier = Modifier.padding(top = 40.dp),
                onClick = {
                    onProceedClicked.invoke()
                },
                border = BorderStroke(2.dp, VolumeOrange),
                contentPadding = PaddingValues(horizontal = 32.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = VolumeOrange),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Next", color = VolumeOrange, fontFamily = lato,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun RenderPreview() {
    FirstPage {

    }
}
