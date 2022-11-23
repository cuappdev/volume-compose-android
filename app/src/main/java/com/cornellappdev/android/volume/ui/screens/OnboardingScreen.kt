package com.cornellappdev.android.volume.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.cornellappdev.android.volume.analytics.EventType
import com.cornellappdev.android.volume.analytics.VolumeEvent
import com.cornellappdev.android.volume.ui.components.onboarding.FirstPage
import com.cornellappdev.android.volume.ui.components.onboarding.SecondPage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(
    proceedHome: () -> Unit
) {
    VolumeEvent.logEvent(EventType.GENERAL, VolumeEvent.START_ONBOARDING)

    val pagerState = rememberPagerState(0)
    var fadePage = remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    HorizontalPager(
        count = 2, state = pagerState,
        modifier = Modifier.fillMaxSize(), userScrollEnabled = false
    ) { page ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (page) {
                0 ->
                    FirstPage {
                        proceedToPageIndex(
                            coroutineScope,
                            page + 1,
                            pagerState,
                            fadePage
                        ).invoke()
                    }
                1 -> SecondPage(fadePage = fadePage) {
                    proceedHome()
                }
            }
        }
    }

    BackHandler(enabled = true) {
        // Back is disabled until onboarding is done.
    }
}

/**
 * Proceeds to the specified pageIndex
 */
@OptIn(ExperimentalPagerApi::class)
fun proceedToPageIndex(
    coroutineScope: CoroutineScope,
    pageIndex: Int,
    pagerState: PagerState,
    fadePage: MutableState<Boolean>
): () -> Unit {
    return {
        coroutineScope.launch {
            pagerState.scrollToPage(pageIndex)
            fadePage.value = true
        }
    }
}
