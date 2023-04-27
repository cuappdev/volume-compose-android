package com.cornellappdev.android.volume.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.cornellappdev.android.volume.R
import com.cornellappdev.android.volume.analytics.EventType
import com.cornellappdev.android.volume.analytics.VolumeEvent
import com.cornellappdev.android.volume.navigation.Routes
import com.cornellappdev.android.volume.ui.components.general.VolumeLinearProgressBar
import com.cornellappdev.android.volume.ui.components.general.shimmerEffect
import com.cornellappdev.android.volume.ui.states.MagazineRetrievalState
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif
import com.cornellappdev.android.volume.ui.viewmodels.IndividualMagazineViewModel
import com.rizzi.bouquet.HorizontalPDFReader
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.rememberHorizontalPdfReaderState

/**
 * This screen represents the screen for viewing individual magazines using the PDF reader
 * from the Bouquet library. It supports shoutouts, bookmarks, and sharing.
 */
@Composable
fun IndividualMagazineScreen(
    magazineId: String,
    individualMagazineViewModel: IndividualMagazineViewModel = hiltViewModel(),
    navController: NavController,
    navSource: String,
) {
    individualMagazineViewModel.queryMagazineById(magazineId)
    val magazineUiState = individualMagazineViewModel.magazineUiState
    var showProgressBar by remember { mutableStateOf(false)}

    Scaffold(
        bottomBar = {
            MakeBottomBar(magazineUiState)
        }, topBar = {
            MakeTopBar(magazineUiState, navController, navSource)
        },
        content = {
            PdfReader(
                magazineUiState = magazineUiState,
                showProgressBar = showProgressBar,
                modifier = Modifier
                    .padding(
                        top = it.calculateTopPadding(),
                        bottom = it.calculateBottomPadding()
                    )
                    .fillMaxWidth()
            )
        }
    )
}
@Composable
fun MakeTopBar(magazineUiState: IndividualMagazineViewModel.IndividualMagazineUiState,
                navController: NavController, navSource: String) {
    when (val magazineState = magazineUiState.magazineState) {
        MagazineRetrievalState.Loading -> {
            TopBar(navController = navController, navSource = navSource)
        }
        MagazineRetrievalState.Error -> {
            TopBar(navController = navController, navSource = navSource)
        }
        is MagazineRetrievalState.Success -> {
            TopBar(magazineState.magazine.publication.name, navController = navController,
            navSource = navSource)
        }
    }

}

@Composable
fun TopBar(publisher: String = "Magazine",
           navController: NavController,
            navSource: String) {
    Row (modifier = Modifier.background(Color(0xFFF9F9F9)).fillMaxWidth()
        , horizontalArrangement = Arrangement.SpaceBetween)
    {
        val arrowWidth = 25.dp
        Icon(painter = painterResource(id = R.drawable.ic_arrow_left),
            contentDescription = null,
            modifier = Modifier
                .width(arrowWidth)
                .offset(x = 16.dp, y=4.dp)
                .clickable {
                    navController.navigate(navSource)
                })
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
        /* This is to help the title column to be automatically aligned with the center by the
         * Space between arrangement */
        Box (modifier = Modifier.width(arrowWidth))
    }
}

/**
 * This composable is responsible for creating the bottom bar based on the current magazine load
 * state. If the magazine is loaded the bottom bar buttons will have functionality and display
 * properly, otherwise they will have default values.
 */
@Composable
fun MakeBottomBar(magazineUiState: IndividualMagazineViewModel.IndividualMagazineUiState, )
{
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
    val space = 80.dp

    val bookmarkModifier = Modifier
        .padding(top = 15.dp, bottom = 29.dp, start = 19.dp)
        .width(space)
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

        Icon(painter = painterResource(id = R.drawable.ic_share_black),
            contentDescription = null,
            modifier = Modifier
                .padding(bottom = 31.dp, top = 11.dp)
                .clickable {
                    shareMagazine(context, id)
                })

        // Shoutouts icon and text
        val shoutoutsModifier = Modifier
            .padding(bottom = 31.dp, start = 11.dp)
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
            .padding(end = 16.dp, top = 10.dp).width(space)) {
            Image(painter = if (hasMaxShoutouts) painterResource(id = R.drawable.ic_shoutout_filled)
                                else painterResource(id = R.drawable.ic_shoutout),
                contentDescription = null,
                modifier = shoutoutsModifier
            )
            Text(
                text = "$shoutouts",
                fontSize = 12.sp,
                modifier = Modifier.offset(x = 5.dp, y = (5).dp)
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
fun PdfReader(magazineUiState: IndividualMagazineViewModel.IndividualMagazineUiState,
              modifier: Modifier,
              showProgressBar: Boolean) {
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
                modifier = if (pdfState.isLoaded) Modifier.fillMaxSize() else Modifier.alpha(0F)
            )


            if (!pdfState.isLoaded) {
                VolumeLinearProgressBar(
                    progress = (pdfState.loadPercent / 100f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .shadow(5.dp)
                )
            }
        }
        
    }
}