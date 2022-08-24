package com.cornellappdev.volume.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cornellappdev.volume.R
import com.cornellappdev.volume.data.models.Article
import com.cornellappdev.volume.data.models.Publication
import com.cornellappdev.volume.ui.components.general.CreateHorizontalArticleRow
import com.cornellappdev.volume.ui.theme.VolumeOrange
import com.cornellappdev.volume.ui.viewmodels.IndividualPublicationViewModel

//"61980a202fef10d6b7f20747"
@Composable
fun IndividualPublicationScreen(individualPublicationViewModel: IndividualPublicationViewModel) {
    val publicationByIDState =
        individualPublicationViewModel.publicationByIDState.collectAsState().value
    val articlesByPublicationIDState =
        individualPublicationViewModel.articlesByPubIDState.collectAsState().value
    Column {

        when (publicationByIDState.publicationRetrievalState) {
            IndividualPublicationViewModel.PublicationRetrievalState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = VolumeOrange)
                }
            }
            IndividualPublicationViewModel.PublicationRetrievalState.Error -> {

            }
            is IndividualPublicationViewModel.PublicationRetrievalState.Success -> {
                PublicationScreen(publicationByIDState.publicationRetrievalState.publication)
            }
        }

        when (articlesByPublicationIDState.articleState) {
            IndividualPublicationViewModel.ArticleState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = VolumeOrange)
                }
            }
            IndividualPublicationViewModel.ArticleState.Error -> {

            }
            is IndividualPublicationViewModel.ArticleState.Success -> {
                PublicationRecent(articlesByPublicationIDState.articleState.articles)
            }
        }
    }
}

//data: PublicationByIDQuery.GetPublicationByID
@Composable
fun PublicationScreen(publication: Publication) {
    //val data= PublicationByIDQuery.GetPublicationByID(data1)

    Box(modifier = Modifier.height(400.dp)) {
        AsyncImage(
            model = publication.backgroundImageURL, modifier = Modifier
                .fillMaxWidth()
                .height(200.dp), contentDescription = null, contentScale = ContentScale.Crop
        )
        AsyncImage(
            model = publication.profileImageURL, modifier = Modifier
                .height(180.dp)
                .padding(top = 60.dp, start = 20.dp)
                .clip(CircleShape), contentDescription = null, contentScale = ContentScale.Crop
        )
        Row {
            Text(publication.name, fontSize = 20.sp)
            Image(
                painter = painterResource(id = R.drawable.ic_follow),
                contentDescription = "", modifier = Modifier.padding(start = 50.dp)
            )
        }
        Row(modifier = Modifier.padding(top = 80.dp)) {
//            Text(data.numArticles.toString() + " -Articles")
            Text(
                publication.shoutouts.toString() + " -Shout-outs",
                modifier = Modifier.padding(start = 5.dp)
            )
        }
        Text(publication.bio, modifier = Modifier.padding(top = 100.dp), fontSize = 15.sp)
        //Social Medias
        PublicationSocial(publication)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_divider),
                contentDescription = "", modifier = Modifier.padding(top = 75.dp)
            )
        }
        Box(modifier = Modifier.padding(top = 140.dp)) {
            Text("Articles")
            Image(
                painter = painterResource(id = R.drawable.ic_underline),
                contentDescription = "", modifier = Modifier.padding(top = 75.dp)
            )
        }
    }
}

@Composable
fun PublicationRecent(articles: List<Article>) {
    LazyColumn {
        items(articles) { article ->
            CreateHorizontalArticleRow(article)
        }
    }
}

//for social medias but I didn't get to completely finish because im not sure if this method works
@Composable
fun PublicationSocial(data: Publication) {
    // Creating an annonated string
    Row {
        Image(
            painter = painterResource(id = R.drawable.ic_facebook),
            contentDescription = "", modifier = Modifier.height(5.dp)
        )
        val mAnnotatedLinkString = buildAnnotatedString {

            // creating a string to display in the Text
            val mStr = "Instagram"

            // word and span to be hyperlinked
            val mStartIndex = 0
            val mEndIndex = 8

            append(mStr)
            addStyle(
                style = SpanStyle(
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline
                ), start = mStartIndex, end = mEndIndex
            )

            // attach a string annotation that
            // stores a URL to the text "link"
            addStringAnnotation(
                tag = "URL",
                annotation = data.socials.toString(),
                start = mStartIndex,
                end = mEndIndex
            )

        }

        // UriHandler parse and opens URI inside
        // AnnotatedString Item in Browse
        val mUriHandler = LocalUriHandler.current

        Column(
            Modifier
                .fillMaxSize()
                .padding(2.dp)
        ) {

            // ???? Clickable text returns position of text
            // that is clicked in onClick callback
            ClickableText(
                text = mAnnotatedLinkString,
                onClick = {
                    mAnnotatedLinkString
                        .getStringAnnotations("URL", it, it)
                        .firstOrNull()?.let { stringAnnotation ->
                            mUriHandler.openUri(stringAnnotation.item)
                        }
                }
            )
        }
    }

}


/**
@Preview
@Composable
fun previewPage(){
PublicationBackground("61980a202fef10d6b7f20747");
}
 */


