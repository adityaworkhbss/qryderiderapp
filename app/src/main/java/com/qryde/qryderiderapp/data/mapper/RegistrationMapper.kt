package com.qryde.qryderiderapp.data.mapper

class RegistrationCheckFailedException(message: String) : Exception(message)

private val COLUMN_SEPARATOR = 14.toChar()

fun String.requireAvailable() {
    val payload = substringAfter('~', missingDelimiterValue = this)
    if (payload.equals("OK", ignoreCase = true)) return

    val message = payload.split(COLUMN_SEPARATOR, limit = 2).getOrNull(1)
    throw RegistrationCheckFailedException(message ?: "This value is not available.")
}
