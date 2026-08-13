package com.qryde.qryderiderapp.presentation.auth.profile

import android.net.Uri

data class CreateProfileUiState(
    val avatarUri: Uri? = null,
    val fullName: String = "",
    val email: String = "",
    val dialCode: String = "+91",
    val phoneNumber: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isTermsAccepted: Boolean = false
) {
    val isSubmitEnabled: Boolean
        get() = fullName.isNotBlank() &&
            phoneNumber.isNotBlank() &&
            password.isNotBlank() &&
            isTermsAccepted
}
