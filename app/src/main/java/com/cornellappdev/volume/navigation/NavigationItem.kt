package com.cornellappdev.volume.navigation

import com.cornellappdev.volume.R

/**
 * Class to represent each tab.
 *
 * @property route matches the tab to the correct screen
 * @property unselectedIconId represents the resource id number for the tab icon when not selected
 * @property selectedIconId represents the resource id number for the tab icon when selected
 * @property title title of tab
 * @property selectedRoutes represents the routes the tab should be considered selected for
 */
sealed class NavigationItem(
    val route: String,
    val unselectedIconId: Int,
    val selectedIconId: Int,
    val title: String,
    val selectedRoutes: Set<String>
) {
    object Home : NavigationItem(
        route = Routes.HOME.route,
        unselectedIconId = R.drawable.ic_volume_bars_gray,
        selectedIconId = R.drawable.ic_volume_bars_orange,
        title = "For You",
        selectedRoutes = setOf(Routes.HOME.route, Routes.WEEKLY_DEBRIEF.route)
    )

    object Magazines : NavigationItem(
        route = Routes.MAGAZINES.route,
        unselectedIconId = R.drawable.ic_magazine_icon_unselected,
        selectedIconId = R.drawable.ic_magazine_icon_selected,
        title = "Magazines",
        selectedRoutes = setOf(Routes.MAGAZINES.route)
    )

    object Publications : NavigationItem(
        route = Routes.PUBLICATIONS.route,
        unselectedIconId = R.drawable.ic_publications_icon_unselected,
        selectedIconId = R.drawable.ic_publications_icon_selected,
        title = "Publications",
        selectedRoutes = setOf(
            Routes.PUBLICATIONS.route,
            "${Routes.INDIVIDUAL_PUBLICATION.route}/{publicationSlug}"
        )
    )

    object Bookmarks : NavigationItem(
        route = Routes.BOOKMARKS.route,
        unselectedIconId = R.drawable.ic_bookmark_gray,
        selectedIconId = R.drawable.ic_bookmark_orange,
        title = "Bookmarks",
        selectedRoutes = setOf(Routes.BOOKMARKS.route)
    )

    companion object {
        val bottomNavTabList = listOf(
            Home,
//            Magazines,
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
    OPEN_MAGAZINE("open_magazine"),
    SETTINGS("settings"),
    ABOUT_US("about_us"),
    WEEKLY_DEBRIEF("weekly_debrief")
}
