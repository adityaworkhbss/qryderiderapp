package com.qryde.qryderiderapp.data.mapper

class SendVerificationCodeFailedException(message: String) : Exception(message)

fun String.requireVerificationCodeSent() {
    val payload = substringAfter('~', missingDelimiterValue = this).trim()

    when {
        payload.equals("OK", ignoreCase = true) -> return
        payload.contains('^') -> {
            val message = payload.substringAfter('^').trim()
            throw SendVerificationCodeFailedException(
                message.ifBlank { "Could not send verification code." }
            )
        }
        else -> throw SendVerificationCodeFailedException("Could not send verification code.")
    }
}
