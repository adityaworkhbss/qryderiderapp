package com.qryde.qryderiderapp.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.qryde.qryderiderapp.presentation.auth.forgotpassword.ForgotPasswordScreen
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
                onLoginSuccess = { userId, isoCode ->
                    navController.navigate(
                        PhoneVerificationRoute(isSignUp = false, userId = userId, isoCode = isoCode)
                    )
                },
                onPasswordResetRequired = {
                    navController.navigate(SetNewPasswordRoute)
                },
                onSignUpClick = {
                    navController.navigate(PhoneVerificationRoute(isSignUp = true))
                },
                onForgotPasswordClick = {
                    navController.navigate(ForgotPasswordRoute)
                }
            )
        }

        composable<PhoneVerificationRoute> { backStackEntry ->
            val route: PhoneVerificationRoute = backStackEntry.toRoute()
            PhoneVerificationScreen(
                userId = route.userId,
                isoCode = route.isoCode,
                onBackToLogin = { navController.popBackStack<LoginRoute>(inclusive = false) },
                onCodeSent = { contact, expectedCode ->
                    navController.navigate(OtpVerificationRoute(route.isSignUp, contact, expectedCode))
                }
            )
        }

        composable<OtpVerificationRoute> { backStackEntry ->
            val route: OtpVerificationRoute = backStackEntry.toRoute()
            OtpVerificationScreen(
                contact = route.contact,
                expectedCode = route.expectedCode,
                onBack = { navController.popBackStack() },
                onVerified = {
                    if (route.isSignUp) {
                        navController.navigate(CreateProfileRoute(phoneNumber = route.contact))
                    } else {
                        onAuthComplete()
                    }
                }
            )
        }

        composable<CreateProfileRoute> { backStackEntry ->
            val route: CreateProfileRoute = backStackEntry.toRoute()
            CreateProfileScreen(
                phoneNumber = route.phoneNumber,
                onBack = { navController.popBackStack() },
                onAccountCreated = onAuthComplete
            )
        }

        composable<ForgotPasswordRoute> {
            ForgotPasswordScreen(
                onBackToLogin = { navController.popBackStack<LoginRoute>(inclusive = false) }
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
