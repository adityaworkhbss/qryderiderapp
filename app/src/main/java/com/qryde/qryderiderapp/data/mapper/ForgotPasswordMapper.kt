package com.qryde.qryderiderapp.data.mapper

class PasswordResetFailedException(message: String) : Exception(message)

private val COLUMN_SEPARATOR = 14.toChar()

fun String.toPasswordResetMessage(): String {
    val payload = substringAfter('~', missingDelimiterValue = this)
    val parts = payload.split(COLUMN_SEPARATOR, limit = 2)
    val status = parts.getOrNull(0).orEmpty()
    val message = parts.getOrNull(1).orEmpty()

    if (!status.equals("OK", ignoreCase = true)) {
        throw PasswordResetFailedException(message.ifBlank { "Could not process this request." })
    }
    return message.ifBlank { "A temporary password has been sent to your registered mobile number." }
}
