package com.cornellappdev.volume.navigation

import com.cornellappdev.volume.R

/**
 * Class to represent each tab.
 *
 * @property route matches the tab to the correct screen
 * @property unselectedIconId represents the resource id number for the tab icon when not selected
 * @property selectedIconId represents the resource id number for the tab icon when selected
 * @property title title of tab
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

    object Publications : NavigationItem(
        Routes.PUBLICATIONS.route,
        R.drawable.ic_publications_icon_selected,
        R.drawable.ic_publications_icon_unselected,
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
 * NavHost/new routes should be added here. Routes can exist independent of tabs (like onboarding).
 */
enum class Routes(override var route: String) : NavUnit {
    HOME("home"),
    MAGAZINES("magazine"),
    PUBLICATIONS("publications"),
    BOOKMARKS("bookmarks"),
    ONBOARDING("onboarding"),
    INDIVIDUAL_PUBLICATION("individual"),
    OPEN_ARTICLE("open_article"),
    SETTINGS("settings"),
    ABOUT_US("about_us"),
    WEEKLY_DEBRIEF("weekly_debrief")
}
