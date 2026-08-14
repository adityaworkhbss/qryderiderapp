package com.qryde.qryderiderapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.qryde.qryderiderapp.presentation.main.MainScaffold
import com.qryde.qryderiderapp.presentation.splash.SplashScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = SplashRoute
    ) {
        composable<SplashRoute> {
            SplashScreen(
                onNavigateToAuth = {
                    navController.navigate(AuthGraphRoute) {
                        popUpTo<SplashRoute> { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(MainGraphRoute) {
                        popUpTo<SplashRoute> { inclusive = true }
                    }
                }
            )
        }
        authNavGraph(navController) {
            navController.navigate(MainGraphRoute) {
                popUpTo<AuthGraphRoute> { inclusive = true }
            }
        }
        composable<MainGraphRoute> {
            MainScaffold()
        }
    }
}
