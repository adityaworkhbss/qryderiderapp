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

// isSignUp steers what happens after OTP verification: LOGIN goes straight
// home, SIGN_UP continues on to CreateProfileRoute (the account doesn't exist
// yet - phone verification comes first). userId/isoCode are only real for
// LOGIN (known from the 5G response); sign-up doesn't have one yet.
@Serializable
data class PhoneVerificationRoute(
    val isSignUp: Boolean = false,
    val userId: String = "",
    val isoCode: String = ""
)

@Serializable
data class OtpVerificationRoute(
    val isSignUp: Boolean = false,
    val contact: String,
    val expectedCode: String = ""
)

// Reached only after sign-up's phone number is verified - phoneNumber carries
// it forward so it can be included in the 100U account-creation call.
@Serializable
data class CreateProfileRoute(val phoneNumber: String = "")

// Forgot password (5FP2) texts a temporary password directly - no in-app OTP
// step, the user just logs back in with it.
@Serializable
data object ForgotPasswordRoute

// Reached when a successful 5G login reports requiresPasswordReset (isUserActive
// == "T", i.e. logging in with a temporary password) - not part of the
// forgot-password request flow itself.
@Serializable
data object SetNewPasswordRoute

// Main graph destinations
@Serializable
data object HomeRoute
