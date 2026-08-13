package com.qryde.qryderiderapp.data.mapper

class DeviceRegistrationFailedException(message: String) : Exception(message)

private val COLUMN_SEPARATOR = 14.toChar()

/**
 * Wire format for 100U: "100U~<result>" where result is one of:
 *  - "NOK" (exact) - generic failure
 *  - "NOK<char14><message>" - failure with a server-provided reason
 *  - "OK^..." ('^'-delimited, only the leading "OK" matters here - the
 *    optional trailing fields are legacy version/affiliation info this app
 *    doesn't use)
 * Anything else is treated as a failure, matching the legacy client's own
 * fallback (it retries registration when the response doesn't match any of
 * the above).
 */
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
