package com.qryde.qryderiderapp.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data object SplashRoute

@Serializable
data object AuthGraphRoute

@Serializable
data object MainGraphRoute

@Serializable
data object LoginRoute

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

@Serializable
data class CreateProfileRoute(val phoneNumber: String = "")

@Serializable
data object ForgotPasswordRoute

@Serializable
data object SetNewPasswordRoute

// Main app - bottom nav tabs + their sub-screens.

// Home is a nested graph (not a plain destination) so AdditionalInformationRoute
// can share the same BookRideViewModel instance via a graph-scoped ViewModel.
@Serializable
data object HomeGraphRoute

@Serializable
data object HomeRoute

@Serializable
data object AdditionalInformationRoute

@Serializable
data object TripsRoute

@Serializable
data class ReceiptRoute(val tripId: String)

@Serializable
data class CancelTripRoute(val tripId: String)

@Serializable
data object PaymentsRoute

@Serializable
data object AddFundsRoute

@Serializable
data object ProfileRoute

@Serializable
data object EditProfileRoute

@Serializable
data object HelpRoute

@Serializable
data object VuiRoute
