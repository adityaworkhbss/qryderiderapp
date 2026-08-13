package com.qryde.qryderiderapp.data.mapper

class DeviceRegistrationFailedException(message: String) : Exception(message)

private val COLUMN_SEPARATOR = 14.toChar()

fun String.requireDeviceRegistered() {
    val payload = substringAfter('~', missingDelimiterValue = this).trim()

    when {
        payload.equals("NOK", ignoreCase = true) ->
            throw DeviceRegistrationFailedException("Could not connect to the server.")
        payload.contains(COLUMN_SEPARATOR) -> {
            val parts = payload.split(COLUMN_SEPARATOR, limit = 2)
            val message = parts.getOrNull(1)?.takeIf { parts[0].equals("NOK", ignoreCase = true) }
            throw DeviceRegistrationFailedException(message ?: "Could not register this device.")
        }
        payload.substringBefore('^').equals("OK", ignoreCase = true) -> return
        else -> throw DeviceRegistrationFailedException("Could not register this device.")
    }
}
