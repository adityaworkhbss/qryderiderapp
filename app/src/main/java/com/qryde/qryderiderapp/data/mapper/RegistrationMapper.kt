package com.qryde.qryderiderapp.data.mapper

class RegistrationCheckFailedException(message: String) : Exception(message)

private val COLUMN_SEPARATOR = 14.toChar()

/**
 * Shared response shape for 100ID (userId availability) and 5E (email
 * availability): "<CMD>~OK" (exact) when available, or
 * "<CMD>~<status><char14><message>" (status anything other than "OK") when
 * not - the message is the server's own "already taken"/"already
 * registered" text.
 */
fun String.requireAvailable() {
    val payload = substringAfter('~', missingDelimiterValue = this)
    if (payload.equals("OK", ignoreCase = true)) return

    val message = payload.split(COLUMN_SEPARATOR, limit = 2).getOrNull(1)
    throw RegistrationCheckFailedException(message ?: "This value is not available.")
}
