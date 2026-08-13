package com.qryde.qryderiderapp.data.mapper

import com.qryde.qryderiderapp.domain.model.LoginSession

/**
 * Thrown for a request the server actively rejected (bad password, locked
 * account, wrong user type, ...) as opposed to a network/parsing failure -
 * its message is the exact server-provided text, safe to show to the user.
 */
class LoginFailedException(message: String) : Exception(message)

/**
 * Wire format for 5G: "5G~<status><char14><payload>". On failure, status is
 * anything other than "OK" and payload is a human-readable message straight
 * from the server. On success, payload is a ~60-field char14-delimited
 * positional record; this only extracts the handful of fields this app
 * actually uses - see the index comments below. Everything else (NEMT,
 * Medicaid, PT1 community, Uber/Lyft, RydeLog, ...) is legacy multi-tenant
 * data this app has no feature for yet.
 */
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
