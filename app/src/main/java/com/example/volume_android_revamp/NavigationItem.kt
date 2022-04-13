package com.example.volume_android_revamp

sealed class NavigationItem(var route: String, var icon: Int, var title: String){
    object Home : NavigationItem("home", R.drawable.ic_volume_bars_gray_tab, "For You")
    object Magazines : NavigationItem("magazines", R.drawable.ic_magazines, "Magazines")
    object Publications : NavigationItem("publications", R.drawable.ic_publications, "Publications")
    object Bookmarks : NavigationItem("bookmarks", R.drawable.ic_bookmark, "Saved")
}