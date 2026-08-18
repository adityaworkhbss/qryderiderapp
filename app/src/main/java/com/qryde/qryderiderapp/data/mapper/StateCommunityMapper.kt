package com.qryde.qryderiderapp.data.mapper

import com.qryde.qryderiderapp.domain.model.StateCommunity

private val RECORD_SEPARATOR = 15.toChar()
private val FIELD_SEPARATOR = 14.toChar()
private const val NULL_PLACEHOLDER = "..."
private const val THREE_RIVERS_ID = "THREERIVERS"
private const val THREE_RIVERS_NAME = "THREE RIVERS"

/**
 * 20SC response: "20SC~<community>(char15)<community>(char15)...", each
 * community a (char14)-separated record of up to 13 fields.
 */
fun String.toStateCommunities(): List<StateCommunity> {
    val payload = substringAfter('~', missingDelimiterValue = "").trim()
    if (payload.isEmpty() || payload.equals("NOK", ignoreCase = true) || payload.equals("NO_DATA_FOUND", ignoreCase = true)) {
        return emptyList()
    }

    return payload.split(RECORD_SEPARATOR)
        .filter { it.isNotBlank() }
        .map { it.toStateCommunity() }
}

private fun String.toStateCommunity(): StateCommunity {
    val fields = split(FIELD_SEPARATOR)
    fun at(index: Int) = fields.getOrNull(index)?.trim().orEmpty()

    val id = at(0)
    val name = if (id.equals(THREE_RIVERS_ID, ignoreCase = true)) THREE_RIVERS_NAME else at(1)
    val description = at(10).let { if (it.isBlank() || it.equals(NULL_PLACEHOLDER, ignoreCase = true)) "" else it }

    return StateCommunity(
        id = id,
        name = name,
        resultCombo = at(2),
        address = at(3),
        webUrl = at(4),
        communityAccess = at(5),
        status = at(6),
        imageUrl = at(7),
        phoneNumber = at(8),
        email = at(9),
        description = description,
        latitude = fields.getOrNull(11)?.trim(),
        longitude = fields.getOrNull(12)?.trim()
    )
}
