package com.qryde.qryderiderapp.presentation.auth.resetpassword

data class SetNewPasswordUiState(
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false
) {
    val passwordsMatch: Boolean
        get() = password.isNotBlank() && password == confirmPassword

    val isSaveEnabled: Boolean
        get() = password.isNotBlank() && confirmPassword.isNotBlank() && passwordsMatch
}
