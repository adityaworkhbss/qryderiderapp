package com.qryde.qryderiderapp.presentation.auth.verification

data class PhoneVerificationUiState(
    val contact: String = ""
) {
    val isSendEnabled: Boolean
        get() = contact.isNotBlank()
}
