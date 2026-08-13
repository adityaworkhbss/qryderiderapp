package com.qryde.qryderiderapp.presentation.navigation

import kotlinx.serialization.Serializable

// Top-level destination, not part of any nested graph.
@Serializable
data object SplashRoute

// Nested-graph marker - never carries business data, only marks where the graph starts.
@Serializable
data object AuthGraphRoute

@Serializable
data object MainGraphRoute

// Auth graph destinations
@Serializable
data object LoginRoute

@Serializable
data object CreateProfileRoute

// Steers what happens after phone verification / OTP: same two screens are
// reused across sign up, plain login (2FA) and forgot-password.
@Serializable
enum class AuthFlow {
    SIGN_UP,
    LOGIN,
    FORGOT_PASSWORD
}

@Serializable
data class PhoneVerificationRoute(val flow: AuthFlow)

@Serializable
data class OtpVerificationRoute(val flow: AuthFlow, val contact: String)

@Serializable
data object SetNewPasswordRoute

// Main graph destinations
@Serializable
data object HomeRoute
