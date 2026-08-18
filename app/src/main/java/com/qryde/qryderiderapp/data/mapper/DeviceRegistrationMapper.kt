package com.qryde.qryderiderapp.data.mapper

import com.qryde.qryderiderapp.domain.model.DeviceRegistrationInfo

class DeviceRegistrationFailedException(message: String) : Exception(message)

private val COLUMN_SEPARATOR = 14.toChar()
private const val USER_FS_ID_FIELD_INDEX = 3

fun String.requireDeviceRegistered(): DeviceRegistrationInfo? {
    val payload = substringAfter('~', missingDelimiterValue = this).trim()

    val fields = when {
        payload.equals("NOK", ignoreCase = true) ->
            throw DeviceRegistrationFailedException("Could not connect to the server.")
        payload.contains(COLUMN_SEPARATOR) -> {
            val parts = payload.split(COLUMN_SEPARATOR, limit = 2)
            val message = parts.getOrNull(1)?.takeIf { parts[0].equals("NOK", ignoreCase = true) }
            throw DeviceRegistrationFailedException(message ?: "Could not register this device.")
        }
        payload.substringBefore('^').equals("OK", ignoreCase = true) -> payload.split('^')
        else -> throw DeviceRegistrationFailedException("Could not register this device.")
    }

    val userFsId = fields.getOrNull(USER_FS_ID_FIELD_INDEX)?.trim()?.takeIf { it.isNotEmpty() }
        ?: return null
    return DeviceRegistrationInfo(userFsId = userFsId)
}
