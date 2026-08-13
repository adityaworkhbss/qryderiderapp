package com.qryde.qryderiderapp.presentation.auth.otp

data class OtpVerificationUiState(
    val otp: String = "",
    val remainingSeconds: Int = RESEND_COOLDOWN_SECONDS
) {
    val isVerifyEnabled: Boolean
        get() = otp.length == OTP_LENGTH

    val canResend: Boolean
        get() = remainingSeconds <= 0

    companion object {
        const val OTP_LENGTH = 6
        const val RESEND_COOLDOWN_SECONDS = 59
    }
}
