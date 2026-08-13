package com.qryde.qryderiderapp.presentation.splash

data class SplashUiState(
    val appName: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed interface SplashNavigationEvent {
    data object ConfigResolved : SplashNavigationEvent
}
