package com.qryde.qryderiderapp.presentation.auth.verification

data class PhoneVerificationUiState(
    val contact: String = "",
    val isSubmitting: Boolean = false
) {
    val isSendEnabled: Boolean
        get() = contact.isNotBlank() && !isSubmitting
}
