package com.qryde.qryderiderapp.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.qryde.qryderiderapp.presentation.auth.login.LoginScreen
import com.qryde.qryderiderapp.presentation.auth.otp.OtpVerificationScreen
import com.qryde.qryderiderapp.presentation.auth.profile.CreateProfileScreen
import com.qryde.qryderiderapp.presentation.auth.resetpassword.SetNewPasswordScreen
import com.qryde.qryderiderapp.presentation.auth.verification.PhoneVerificationScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    onAuthComplete: () -> Unit
) {
    navigation<AuthGraphRoute>(startDestination = LoginRoute) {
        composable<LoginRoute> {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(PhoneVerificationRoute(AuthFlow.LOGIN))
                },
                onSignUpClick = {
                    navController.navigate(CreateProfileRoute)
                },
                onForgotPasswordClick = {
                    navController.navigate(PhoneVerificationRoute(AuthFlow.FORGOT_PASSWORD))
                }
            )
        }

        composable<CreateProfileRoute> {
            CreateProfileScreen(
                onBack = { navController.popBackStack() },
                onSubmit = {
                    navController.navigate(PhoneVerificationRoute(AuthFlow.SIGN_UP))
                }
            )
        }

        composable<PhoneVerificationRoute> { backStackEntry ->
            val route: PhoneVerificationRoute = backStackEntry.toRoute()
            PhoneVerificationScreen(
                onBackToLogin = { navController.popBackStack<LoginRoute>(inclusive = false) },
                onSendOtp = { contact ->
                    navController.navigate(OtpVerificationRoute(route.flow, contact))
                }
            )
        }

        composable<OtpVerificationRoute> { backStackEntry ->
            val route: OtpVerificationRoute = backStackEntry.toRoute()
            OtpVerificationScreen(
                contact = route.contact,
                onBack = { navController.popBackStack() },
                onVerified = {
                    when (route.flow) {
                        AuthFlow.FORGOT_PASSWORD -> navController.navigate(SetNewPasswordRoute)
                        AuthFlow.SIGN_UP, AuthFlow.LOGIN -> onAuthComplete()
                    }
                }
            )
        }

        composable<SetNewPasswordRoute> {
            SetNewPasswordScreen(
                onBack = { navController.popBackStack() },
                onSaved = onAuthComplete
            )
        }
    }
}
