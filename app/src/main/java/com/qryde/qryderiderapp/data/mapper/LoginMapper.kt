package com.qryde.qryderiderapp.data.mapper

import com.qryde.qryderiderapp.domain.model.LoginSession

class LoginFailedException(message: String) : Exception(message)

fun String.toLoginSession(): LoginSession {
    val payload = substringAfter(COMMAND_SEPARATOR, missingDelimiterValue = this)
    val statusAndRest = payload.split(COLUMN_SEPARATOR, limit = 2)
    val status = statusAndRest.getOrNull(0).orEmpty()
    val rest = statusAndRest.getOrNull(1).orEmpty()

    if (!status.equals("OK", ignoreCase = true)) {
        throw LoginFailedException(rest.ifBlank { "Login failed." })
    }

    val fields = rest.split(COLUMN_SEPARATOR)
    fun field(index: Int): String = fields.getOrNull(index)?.trim().orEmpty()

    if (field(USER_TYPE_INDEX).toIntOrNull() != RIDER_USER_TYPE) {
        throw LoginFailedException("This account is not authorized to use the rider app.")
    }

    return LoginSession(
        userId = field(0),
        phoneNumber = field(1),
        supplierId = field(2),
        userName = field(5),
        email = field(6),
        isoCode = field(19),
        requiresPasswordReset = field(16).equals("T", ignoreCase = true)
    )
}

private const val COMMAND_SEPARATOR = '~'
private val COLUMN_SEPARATOR = 14.toChar()
private const val USER_TYPE_INDEX = 4
private const val RIDER_USER_TYPE = 1
