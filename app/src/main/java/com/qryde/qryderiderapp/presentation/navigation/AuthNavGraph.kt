package com.qryde.qryderiderapp.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import androidx.navigation.compose.composable
import com.qryde.qryderiderapp.presentation.auth.login.LoginScreen

/**
 * Everything under here is a Presentation concern: it knows where the user can go
 * after logging in, not how login is validated. Business rules live in Domain.
 */
fun NavGraphBuilder.authNavGraph(navController: NavController) {
    navigation<AuthGraphRoute>(startDestination = LoginRoute) {
        composable<LoginRoute> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(MainGraphRoute) {
                        popUpTo<AuthGraphRoute> { inclusive = true }
                    }
                }
            )
        }
    }
}
