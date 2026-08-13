package com.qryde.qryderiderapp.data.mapper

import com.qryde.qryderiderapp.data.remote.dto.CommunityInfoResponse
import com.qryde.qryderiderapp.domain.model.Community
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private val EMPTY_RESPONSE_VALUES = setOf("", "null", "nok", "no_data_found")

fun String.toCommunities(json: Json): List<Community> {
    val payload = substringAfter('~', missingDelimiterValue = this).trim()
    if (payload.lowercase() in EMPTY_RESPONSE_VALUES) return emptyList()

    val response = runCatching { json.decodeFromString<CommunityInfoResponse>(payload) }
        .getOrDefault(CommunityInfoResponse())

    return response.CommunityInfo.map { dto ->
        Community(
            id = dto.CommunityId,
            name = dto.CommunityName,
            isPreferred = dto.pref_comm.equals("Y", ignoreCase = true),
            resultCombo = dto.result_combo,
            address = dto.address,
            webUrl = dto.homeurl,
            communityAccess = dto.comm_access,
            requestType = dto.request_type,
            phone = dto.Phone,
            email = dto.email,
            logoUrl = dto.logo,
            description = dto.description,
            latitude = dto.latitude,
            longitude = dto.longitude
        )
    }
}
