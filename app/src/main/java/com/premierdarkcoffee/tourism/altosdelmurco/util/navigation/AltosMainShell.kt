package com.premierdarkcoffee.tourism.altosdelmurco.util.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.view.AdventureScreen
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionState
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.view.BookingsScreen
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.presentation.view.HomeScreen
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.presentation.view.ProfileScreen
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.RestaurantScreen
import com.premierdarkcoffee.tourism.altosdelmurco.util.theme.ThemeMode

private enum class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit,
) {
    HOME(
        route = "home",
        label = "Inicio",
        icon = { Icon(Icons.Rounded.Home, contentDescription = null) },
    ),
    RESTAURANT(
        route = "restaurant",
        label = "Restaurante",
        icon = { Icon(Icons.Rounded.Restaurant, contentDescription = null) },
    ),
    ADVENTURE(
        route = "adventure",
        label = "Aventura",
        icon = { Icon(Icons.Rounded.Explore, contentDescription = null) },
    ),
    BOOKINGS(
        route = "bookings",
        label = "Reservas",
        icon = { Icon(Icons.Rounded.CalendarMonth, contentDescription = null) },
    ),
    PROFILE(
        route = "profile",
        label = "Perfil",
        icon = { Icon(Icons.Rounded.Person, contentDescription = null) },
    ),
}

@Composable
fun AltosMainShell(
    sessionState: SessionState.Authenticated,
    currentThemeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit,
) {
    val navController = rememberNavController()
    val destinations = TopLevelDestination.entries
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                destinations.forEach { destination ->
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == destination.route } == true

                    NavigationBarItem(
                        selected = selected,
                        onClick = { navController.navigateTopLevel(destination.route) },
                        icon = destination.icon,
                        label = { Text(destination.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.HOME.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(TopLevelDestination.HOME.route) {
                HomeScreen(sessionState = sessionState)
            }
            composable(TopLevelDestination.RESTAURANT.route) {
                RestaurantScreen(sessionState = sessionState)
            }
            composable(TopLevelDestination.ADVENTURE.route) {
                AdventureScreen(sessionState = sessionState)
            }
            composable(TopLevelDestination.BOOKINGS.route) {
                BookingsScreen(sessionState = sessionState)
            }
            composable(TopLevelDestination.PROFILE.route) {
                ProfileScreen(
                    sessionState = sessionState,
                    currentThemeMode = currentThemeMode,
                    onThemeModeSelected = onThemeModeSelected,
                )
            }
        }
    }
}

private fun NavHostController.navigateTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        restoreState = true
        launchSingleTop = true
    }
}
