package com.cornellappdev.android.volume.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.volume.R
import com.cornellappdev.android.volume.analytics.EventType
import com.cornellappdev.android.volume.analytics.VolumeEvent
import com.cornellappdev.android.volume.navigation.Routes
import com.cornellappdev.android.volume.ui.components.general.shimmerEffect
import com.cornellappdev.android.volume.ui.states.MagazineRetrievalState
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif
import com.cornellappdev.android.volume.ui.viewmodels.IndividualMagazineViewModel
import com.rizzi.bouquet.HorizontalPDFReader
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.rememberHorizontalPdfReaderState

private const val TAG = "IndividualMagazineScreen"

@Composable
fun IndividualMagazineScreen(
    magazineId: String,
    individualMagazineViewModel: IndividualMagazineViewModel = hiltViewModel(),
) {
    individualMagazineViewModel.queryMagazineById(magazineId)
    val magazineUiState = individualMagazineViewModel.magazineUiState

    Scaffold(
        bottomBar = {
            MakeBottomBar(magazineUiState)
        }, topBar = {
            MakeTopBar(magazineUiState)
        },
        content = {
            PdfReader(
                magazineUiState = magazineUiState,
                modifier = Modifier.padding(
                    top = it.calculateTopPadding(),
                    bottom = it.calculateBottomPadding()
                )
            )
        }
    )
}
@Composable
fun MakeTopBar(magazineUiState: IndividualMagazineViewModel.IndividualMagazineUiState) {
    when (val magazineState = magazineUiState.magazineState) {
        MagazineRetrievalState.Loading -> {
            TopBar()
        }
        MagazineRetrievalState.Error -> {
            TopBar()
        }
        is MagazineRetrievalState.Success -> {
            TopBar(magazineState.magazine.publication.name)
        }
    }

}

@Composable
fun TopBar(publisher: String = "Magazine") {
    Row (modifier = Modifier.background(Color(0xFFF9F9F9))) {
        Icon(painter = painterResource(id = R.drawable.ic_arrow_left),
            contentDescription = null,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp))
        Spacer(modifier = Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 8.dp)) {
            Text(
                fontSize = 15.sp,
                fontFamily = notoserif,
                fontWeight = FontWeight.Medium,
                text = publisher,
                textAlign = TextAlign.Center
            )
            Text(
                color = Color(0xFF979797),
                fontSize = 10.sp,
                fontFamily = lato,
                fontWeight = FontWeight.Normal,
                text = "Reading in Volume"
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(painter = painterResource(id = R.drawable.ic_menu),
            contentDescription = null,
            modifier = Modifier.padding(end = 20.dp, top = 16.dp).clickable {
                // TODO implement table of contents
            })
    }
}

/**
 * This composable is responsible for creating the bottom bar based on the current magazine load
 * state. If the magazine is loaded the bottom bar buttons will have functionality and display
 * properly, otherwise they will have default values.
 */
@Composable
fun MakeBottomBar(magazineUiState: IndividualMagazineViewModel.IndividualMagazineUiState) {
    when (val magazineState = magazineUiState.magazineState) {
        MagazineRetrievalState.Loading -> {
            BottomBar()
        }
        MagazineRetrievalState.Error -> {
            BottomBar()
        }
        is MagazineRetrievalState.Success -> {
            BottomBar(
                shoutouts = magazineUiState.shoutoutCount,
                isBookmarked = magazineUiState.isBookmarked,
                id = magazineState.magazine.id,
                hasMaxShoutouts = magazineUiState.hasMaxShoutouts,
            )
        }
    }
}


@Composable
@Preview
fun BottomBar(shoutouts: Int = 0,
              isBookmarked: Boolean = false,
              hasMaxShoutouts: Boolean = false,
              id: String = "",
              individualMagazineViewModel: IndividualMagazineViewModel = hiltViewModel())
{
    val context = LocalContext.current

    val bookmarkModifier = Modifier
        .padding(top = 15.dp, bottom = 29.dp, start = 19.dp)
        .clickable {
            individualMagazineViewModel.bookmarkMagazine()
        }

    Row (modifier = Modifier
        .background(color = Color.White)
        .fillMaxWidth()
        , horizontalArrangement = Arrangement.SpaceBetween) {

        // Bookmark icon
        if (isBookmarked) {
            Icon(
                Icons.Filled.Bookmark,
                contentDescription = "Bookmark article",
                tint = VolumeOrange,
                modifier = bookmarkModifier
            )
        }
        else {
            Icon(
                Icons.Outlined.BookmarkBorder,
                contentDescription = "Bookmark article",
                tint = Color.Black,
                modifier = bookmarkModifier
            )
        }

        // Share icon
        Icon(painter = painterResource(id = R.drawable.ic_share_black),
            contentDescription = null,
            modifier = Modifier
                .padding(bottom = 31.dp, top = 11.dp)
                .clickable {
                    shareMagazine(context, id)
                })

        // Shoutouts icon and text
        val shoutoutsModifier = Modifier
            .padding(bottom = 31.dp, top = 11.dp)
            .clickable {
                if (!hasMaxShoutouts) {
                    VolumeEvent.logEvent(
                        EventType.MAGAZINE,
                        VolumeEvent.SHOUTOUT_MAGAZINE,
                        id = id
                    )
                    individualMagazineViewModel.shoutoutMagazine()
                }
            }

        Row (modifier = Modifier
            .padding(end = 16.dp)) {
            Image(painter = if (hasMaxShoutouts) painterResource(id = R.drawable.ic_shoutout_filled)
                                else painterResource(id = R.drawable.ic_shoutout),
                contentDescription = null,
                modifier = shoutoutsModifier)
            Text(
                text = "$shoutouts",
                modifier = Modifier.padding(start=10.dp, bottom = 25.dp, top = 15.dp),
                fontSize = 12.sp
            )
        }

    }
}


private fun shareMagazine(context: Context, id: String) {
    VolumeEvent.logEvent(
        EventType.MAGAZINE,
        VolumeEvent.OPEN_MAGAZINE,
        id = id
    )
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(
            Intent.EXTRA_TEXT,
            "Look at this magazine I found on Volume: volume://${Routes.OPEN_ARTICLE.route}/${id}"
        )
    }

    context.startActivity(Intent.createChooser(intent, "Share To:"))
}


@Composable
fun PdfReader(magazineUiState: IndividualMagazineViewModel.IndividualMagazineUiState, modifier: Modifier) {
    when (val magazineByIdState = magazineUiState.magazineState) {
        MagazineRetrievalState.Loading -> {
            Box(modifier = Modifier
                .fillMaxSize()
                .shimmerEffect())
        }
        MagazineRetrievalState.Error -> {
            // TODO retry prompt
        }
        is MagazineRetrievalState.Success -> {
            val magazine = magazineByIdState.magazine
            val pdfState = rememberHorizontalPdfReaderState(
                resource = ResourceType.Remote(magazine.pdfURL),
                isZoomEnable = true
            )

            HorizontalPDFReader(
                state = pdfState,
                modifier = if (pdfState.isLoaded) modifier.fillMaxSize() else modifier
                    .fillMaxSize()
                    .shimmerEffect()
            )
        }
    }
}
