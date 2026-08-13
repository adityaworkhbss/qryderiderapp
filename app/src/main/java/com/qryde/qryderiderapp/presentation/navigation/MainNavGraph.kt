package com.qryde.qryderiderapp.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import androidx.navigation.compose.composable
import com.qryde.qryderiderapp.presentation.home.HomeScreen

fun NavGraphBuilder.mainNavGraph(navController: NavController) {
    navigation<MainGraphRoute>(startDestination = HomeRoute) {
        composable<HomeRoute> {
            HomeScreen()
        }
    }
}
