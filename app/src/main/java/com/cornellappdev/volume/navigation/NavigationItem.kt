package com.cornellappdev.volume.navigation

import com.cornellappdev.volume.R

/**
 * Class to represent each tab.
 *
 * The route is needed to match the tab to the correct screen.
 * The iconId is the resource id number for the drawable for the icon of the tab.
 */
sealed class NavigationItem(
    val route: String,
    val unselectedIconId: Int,
    val selectedIconId: Int,
    val title: String
) {
    object Home : NavigationItem(
        Routes.HOME.route,
        R.drawable.ic_volume_bars_gray,
        R.drawable.ic_volume_bars_orange,
        "For You"
    )

    object Magazines : NavigationItem(
        Routes.MAGAZINES.route,
        R.drawable.ic_magazines_gray,
        R.drawable.ic_magazines_orange,
        "Magazines"
    )

    object Publications : NavigationItem(
        Routes.PUBLICATIONS.route,
        R.drawable.ic_book_gray,
        R.drawable.ic_book_orange,
        "Publications"
    )

    object Bookmarks :
        NavigationItem(
            Routes.BOOKMARKS.route,
            R.drawable.ic_bookmark_gray,
            R.drawable.ic_bookmark_orange,
            "Saved"
        )

    companion object {
        val bottomNavTabList = listOf(
            Home,
            Magazines,
            Publications,
            Bookmarks
        )
    }
}

/**
 * All NavUnit must have a route (which specifies where to
 * navigate to).
 */
interface NavUnit {
    val route: String
}

/**
 * Contains information about all known routes. These should correspond to routes in our
 * NavHost/new routes should be added here.
 */
enum class Routes(override var route: String) : NavUnit {
    HOME("HOME"),
    MAGAZINES("MAGAZINE"),
    PUBLICATIONS("PUBLICATIONS"),
    BOOKMARKS("BOOKMARKS"),
    ONBOARDING("ONBOARDING")
}
