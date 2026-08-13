package com.qryde.qryderiderapp.presentation.auth.profile

import android.net.Uri

enum class AvailabilityStatus {
    UNKNOWN,
    CHECKING,
    AVAILABLE,
    UNAVAILABLE
}

data class CreateProfileUiState(
    val avatarUri: Uri? = null,
    val firstName: String = "",
    val lastName: String = "",
    val userId: String = "",
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isTermsAccepted: Boolean = false,
    val userIdStatus: AvailabilityStatus = AvailabilityStatus.UNKNOWN,
    val userIdStatusMessage: String? = null,
    val emailStatus: AvailabilityStatus = AvailabilityStatus.UNKNOWN,
    val emailStatusMessage: String? = null,
    val isSubmitting: Boolean = false
) {
    val isSubmitEnabled: Boolean
        get() = firstName.isNotBlank() &&
            lastName.isNotBlank() &&
            userId.isNotBlank() &&
            email.isNotBlank() &&
            password.isNotBlank() &&
            isTermsAccepted &&
            userIdStatus == AvailabilityStatus.AVAILABLE &&
            emailStatus == AvailabilityStatus.AVAILABLE &&
            !isSubmitting
}
