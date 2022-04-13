package com.example.volume_android_revamp

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.volume_android_revamp.navigation.NavigationItem
import com.example.volume_android_revamp.state.State
import com.example.volume_android_revamp.viewmodels.HomeTabViewModel

@Composable
fun HomeScreen(homeTabViewModel: HomeTabViewModel){
  val context = LocalContext.current
  VolumeTitle(context)
    Log.d("HomeTab", "check")
    when (val state = homeTabViewModel.trendingArticlesState.collectAsState().value){
        is State.Success<TrendingArticlesQuery.Data>-> Log.d("HomeTab", state.value.toString())
        is State.Error<TrendingArticlesQuery.Data> -> Log.d("HomeTab", "EROROROROROROR")
        is State.Loading<TrendingArticlesQuery.Data> -> Log.d("HomeTab", "loading")
        is State.Empty<TrendingArticlesQuery.Data> -> Log.d("HomeTab", "empty")
    }
}

@Composable
fun VolumeTitle(context:Context){
    Row {
        Image(painter = painterResource(R.drawable.volume_title), contentDescription = context.getString(R.string.volume_title))
    }
}
//@Preview
//@Composable
//fun PreviewHomeScreen(
////    homeViewModel: HomeViewModel
//){
//    HomeScreen()
//}