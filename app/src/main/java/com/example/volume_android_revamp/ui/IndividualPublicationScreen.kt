package com.example.volume_android_revamp.ui

import android.content.ClipData
import android.media.Image
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.volume_android_revamp.*
import com.example.volume_android_revamp.R
import com.example.volume_android_revamp.state.State
import com.example.volume_android_revamp.type.Article
import com.example.volume_android_revamp.type.Publication
import com.example.volume_android_revamp.viewmodels.HomeTabViewModel
import com.example.volume_android_revamp.viewmodels.IndividualPublicationViewModel
import org.w3c.dom.Text

//"61980a202fef10d6b7f20747"
@Composable
fun IndividualPublicationScreen(individualPublicationViewModel: IndividualPublicationViewModel) {
    val publicationByIDState =
        individualPublicationViewModel.publicationByIDState.collectAsState().value
    val articlesByPublicationIDState =
        individualPublicationViewModel.articlesByPubIDState.collectAsState().value
    Column() {

        publicationByIDState.value?.getPublicationByID?.let {
            when (val publicationByIDState =
                individualPublicationViewModel.publicationByIDState.collectAsState().value) {
                is State.Success<PublicationByIDQuery.Data> ->
                    publicationByIDState.value!!.getPublicationByID?.let { it1 ->
                        PublicationScreen(
                            data = it1
                        )
                    }
                is State.Error<PublicationByIDQuery.Data> -> Log.d(
                    "HomeTab",
                    "EROROROROROROR"
                )
                is State.Loading<PublicationByIDQuery.Data> -> Log.d("HomeTab", "loading aa")
                is State.Empty<PublicationByIDQuery.Data> -> Log.d("HomeTab", "empty aa")
            }

        }



        articlesByPublicationIDState.value?.getArticlesByPublicationID?.let {
            when (val articlesByPublicationIDState =
                individualPublicationViewModel.articlesByPubIDState.collectAsState().value) {
                is State.Success<ArticlesByPublicationIDQuery.Data> ->
                    articlesByPublicationIDState.value!!.getArticlesByPublicationID?.let { it1 ->
                        PublicationRecent(
                            data = it1
                        )
                    }
                is State.Error<ArticlesByPublicationIDQuery.Data> -> Log.d(
                    "HomeTab",
                    "EROROROROROROR"
                )
                is State.Loading<ArticlesByPublicationIDQuery.Data> -> Log.d(
                    "HomeTab",
                    "loading aa"
                )
                is State.Empty<ArticlesByPublicationIDQuery.Data> -> Log.d("HomeTab", "empty aa")
            }
        }

    }
}
//data: PublicationByIDQuery.GetPublicationByID
@Composable
fun PublicationScreen(data: PublicationByIDQuery.GetPublicationByID) {
    //val data= PublicationByIDQuery.GetPublicationByID(data1)

        Box(modifier=Modifier.height(400.dp)){
            AsyncImage(
                model = data.backgroundImageURL, modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    , contentDescription = null, contentScale = ContentScale.Crop)
            AsyncImage(model = data.profileImageURL, modifier= Modifier
                .height(180.dp)
                .padding(top = 60.dp, start = 20.dp)
                .clip(CircleShape), contentDescription = null, contentScale = ContentScale.Crop)
            Row{
                Text(data.name, fontSize = 20.sp);
                Image(painter= painterResource(id = R.drawable.ic_follow),
                    contentDescription = "", modifier= Modifier.padding(start = 50.dp)
                )
            }
            Row(modifier=Modifier.padding(top=80.dp)){
                Text(data.numArticles.toString()+" -Articles");
                Text(data.shoutouts.toString()+" -Shout-outs", modifier=Modifier.padding(start=5.dp));
            }
            Text(data.bio, modifier=Modifier.padding(top=100.dp), fontSize = 15.sp)
            //Social Medias
            PublicationSocial(data)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.ic_divider),
                    contentDescription = "", modifier = Modifier.padding(top = 75.dp)
                )
            }
            Box(modifier=Modifier.padding(top=140.dp)){
                Text("Articles")
                Image(
                    painter = painterResource(id = R.drawable.ic_underline),
                    contentDescription = "", modifier = Modifier.padding(top = 75.dp)
                )
            }
    }
}

@Composable
fun PublicationRecent(data: List<ArticlesByPublicationIDQuery.GetArticlesByPublicationID>){
    LazyColumn(){
        for (i in 1..data.size){
            item() {
                Row() {
                    Text(data[i].title, fontSize = 20.sp)
                    AsyncImage(
                        model = data[i].imageURL,
                        modifier = Modifier
                            .height(180.dp)
                            .width(180.dp)
                            .padding(start = 50.dp),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
                Row(modifier = Modifier.padding(20.dp)) {
                    Text(data[i].date.toString() + " • " + data[i].shoutouts.toString() + "shout-outs")
                }
            }
        }
    }
}

//for social medias but I didn't get to completely finish because im not sure if this method works
@Composable
fun PublicationSocial(data: PublicationByIDQuery.GetPublicationByID){
    // Creating an annonated string
    Row(){
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
                .padding(2.dp)) {

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


