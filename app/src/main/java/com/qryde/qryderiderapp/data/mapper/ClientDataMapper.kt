package com.qryde.qryderiderapp.data.mapper

import com.qryde.qryderiderapp.domain.model.NemtClientInfo

private const val NULL_PLACEHOLDER = "..."
private const val FIELD_SEPARATOR = "^"

fun String.toNemtClientInfo(): NemtClientInfo? {
    val payload = substringAfter('~', missingDelimiterValue = "").trim()
    if (payload.isEmpty() || payload.equals(NULL_PLACEHOLDER, ignoreCase = true)) {
        return null
    }

    val fields = payload.split(FIELD_SEPARATOR)
    if (fields.size <= 1) {
        return null
    }
    fun at(index: Int) = fields.getOrNull(index)?.trim().orEmpty()

    val nemtPortalId = at(9)
    val tenantId = nemtPortalId.substringAfter('-', missingDelimiterValue = "")
        .ifBlank { null }
        ?.uppercase()

    return NemtClientInfo(
        nemtClientId = at(0),
        dateOfBirth = at(3),
        medicaidNumber = at(5),
        nemtRegionId = at(7),
        nemtPortalId = nemtPortalId,
        tenantId = tenantId
    )
}
