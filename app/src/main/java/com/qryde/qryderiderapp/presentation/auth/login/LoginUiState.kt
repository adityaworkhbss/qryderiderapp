package com.qryde.qryderiderapp.presentation.auth.login

data class LoginUiState(
    val userId: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false
) {
    val isLoginEnabled: Boolean
        get() = userId.isNotBlank() && password.isNotBlank()
}
