package com.qryde.qryderiderapp.data.mapper

class SendVerificationCodeFailedException(message: String) : Exception(message)

/**
 * Wire format for 100UV: "100UV~<result>" where result is "OK", "NOK", or
 * "NOK^<message>" - note this one uses '^' for its error message, not the
 * char14 delimiter every other command in this app uses.
 */
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
