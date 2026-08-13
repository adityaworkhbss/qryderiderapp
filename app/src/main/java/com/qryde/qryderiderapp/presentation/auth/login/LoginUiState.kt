package com.qryde.qryderiderapp.presentation.auth.login

data class LoginUiState(
    val phoneNumber: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface LoginNavigationEvent {
    data object LoginSuccess : LoginNavigationEvent
}
