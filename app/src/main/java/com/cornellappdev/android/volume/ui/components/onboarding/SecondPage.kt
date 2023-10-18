package com.cornellappdev.android.volume.ui.components.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.volume.R
import com.cornellappdev.android.volume.analytics.EventType
import com.cornellappdev.android.volume.analytics.NavigationSource
import com.cornellappdev.android.volume.analytics.VolumeEvent
import com.cornellappdev.android.volume.ui.components.general.CreatePublicationRow
import com.cornellappdev.android.volume.ui.components.general.ErrorState
import com.cornellappdev.android.volume.ui.states.PublicationsRetrievalState
import com.cornellappdev.android.volume.ui.theme.GrayOne
import com.cornellappdev.android.volume.ui.theme.VolumeOffWhite
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif
import com.cornellappdev.android.volume.ui.viewmodels.OnboardingViewModel
import kotlinx.coroutines.delay

@Composable
fun SecondPage(
    onboardingViewModel: OnboardingViewModel = hiltViewModel(),
    fadePage: MutableState<Boolean>,
    onProceedClicked: () -> Unit,
) {
    val onboardingUiState = onboardingViewModel.onboardingUiState
    val lazyListState = rememberLazyListState()
    var proceedEnabled by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = fadePage.value,
        enter = fadeIn(
            initialAlpha = 0f,
            animationSpec = tween(durationMillis = 2500)
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = 2500)
        )
    ) {
        Column(
            modifier = Modifier.wrapContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.volume_title),
                contentDescription = null
            )

            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = "Follow student publications that you",
                textAlign = TextAlign.Center,
                fontFamily = notoserif,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
            Text(
                modifier = Modifier.padding(bottom = 30.dp),
                text = "are interested in",
                textAlign = TextAlign.Center,
                fontFamily = notoserif,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
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

            when (val publicationsState = onboardingUiState.publicationsState) {
                PublicationsRetrievalState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = VolumeOrange)
                    }
                }

                PublicationsRetrievalState.Error -> {
                    ErrorState()
                }

                is PublicationsRetrievalState.Success -> {
                    BoxWithConstraints {
                        LazyColumn(
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .heightIn(max = maxHeight - 200.dp),
                            state = lazyListState,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            items(
                                items = publicationsState.publications
                            ) { publication ->
                                // Clicking on row IN onboarding should not lead to IndividualPublicationScreen. They are not
                                // an official user yet so they shouldn't be interacting with the articles.
                                CreatePublicationRow(publication = publication) { publicationFromCallback, isFollowing ->
                                    if (isFollowing) {
                                        onboardingViewModel.addPublicationToFollowed(
                                            publicationFromCallback.slug
                                        )
                                        VolumeEvent.logEvent(
                                            EventType.PUBLICATION,
                                            VolumeEvent.FOLLOW_PUBLICATION,
                                            NavigationSource.ONBOARDING,
                                            publicationFromCallback.slug
                                        )
                                    } else {
                                        onboardingViewModel.removePublicationFromFollowed(
                                            publicationFromCallback.slug
                                        )
                                        VolumeEvent.logEvent(
                                            EventType.PUBLICATION,
                                            VolumeEvent.UNFOLLOW_PUBLICATION,
                                            NavigationSource.ONBOARDING,
                                            publicationFromCallback.slug
                                        )
                                    }

                                    proceedEnabled =
                                        onboardingViewModel.setOfPubsFollowed.isNotEmpty()
                                }
                            }
                        }

                        // Gradient overlay to the bottom of the Search results LazyColumn
                        androidx.compose.animation.AnimatedVisibility(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .height(100.dp)
                                .fillMaxWidth(),
                            enter = fadeIn(),
                            exit = fadeOut(),
                            // The gradient overlay is only visible when the user hasn't scrolled to the end
                            // so the gradient isn't blocking the final publication
                            visible = !lazyListState.isScrolledToTheEnd()
                        ) {
                            Spacer(
                                Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                VolumeOffWhite
                                            )
                                        )
                                    )
                            )
                        }
                    }
                }
            }


            val proceedColor by animateColorAsState(
                targetValue = if (proceedEnabled) VolumeOrange else GrayOne,
                animationSpec = tween(durationMillis = 500)
            )

            OutlinedButton(
                modifier = Modifier.padding(top = 40.dp),
                enabled = proceedEnabled,
                onClick = {
                    onboardingViewModel.createUser()
                },
                border = BorderStroke(2.dp, proceedColor),
                contentPadding = PaddingValues(horizontal = 32.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = proceedColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Start reading",
                    color = proceedColor,
                    fontFamily = lato,
                    letterSpacing = (-1).sp,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }

    when (onboardingUiState.createUserState) {
        OnboardingViewModel.CreateUserState.Error -> {
            ErrorState()
        }

        OnboardingViewModel.CreateUserState.Pending -> {
            // Wait to be finished
        }

        is OnboardingViewModel.CreateUserState.Success -> {
            // Fades the page out
            fadePage.value = false
            onboardingViewModel.updateOnboardingCompleted()
            LaunchedEffect(Unit) {
                delay(2500L)
                onProceedClicked.invoke()
            }
        }
    }
}

/**
 * Detects when the given LazyList is scrolled to the start.
 */
fun LazyListState.isScrolledToTheStart() =
    layoutInfo.visibleItemsInfo.firstOrNull()?.index == 0


/**
 * Detects when the given LazyList has scrolled to the end.
 */
fun LazyListState.isScrolledToTheEnd() =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
