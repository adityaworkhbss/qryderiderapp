package com.qryde.qryderiderapp.presentation.auth.forgotpassword

data class ForgotPasswordUiState(
    val userId: String = "",
    val email: String = "",
    val isSubmitting: Boolean = false
) {
    val isSendEnabled: Boolean
        get() = userId.isNotBlank() && email.isNotBlank() && !isSubmitting
}
