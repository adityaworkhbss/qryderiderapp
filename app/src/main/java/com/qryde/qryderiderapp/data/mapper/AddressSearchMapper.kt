package com.qryde.qryderiderapp.data.mapper

import com.qryde.qryderiderapp.domain.model.AddressSuggestion
import com.qryde.qryderiderapp.domain.model.CommunitySiteConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val FETCH_ADDRESSES_FROM_RL_KEY = "FetchAddressesFromRL"
private const val RL_CONFIG_SEPARATOR = "^"
private const val RL_ENABLED_FLAG = "Y"

data class RlAddressSearchConfig(val baseUrl: String, val token: String)

/**
 * "FetchAddressesFromRL" is "<Y|N>^<baseUrl>^<token>" - same per-community
 * CP_SiteConfig blob as ClientType/TenantId, just a different key.
 */
fun CommunitySiteConfig.rlAddressSearchConfig(): RlAddressSearchConfig? {
    val parts = valueFor(FETCH_ADDRESSES_FROM_RL_KEY)?.split(RL_CONFIG_SEPARATOR) ?: return null
    if (parts.size != 3 || !parts[0].equals(RL_ENABLED_FLAG, ignoreCase = true)) return null

    val baseUrl = parts[1].trim()
    val token = parts[2].trim()
    if (baseUrl.isBlank() || token.isBlank()) return null
    return RlAddressSearchConfig(baseUrl, token)
}

@Serializable
private data class TypeAheadAddressResponse(val data: TypeAheadAddressData = TypeAheadAddressData())

@Serializable
private data class TypeAheadAddressData(val addressData: List<TypeAheadAddressDto> = emptyList())

@Serializable
private data class TypeAheadAddressDto(
    val ALIAS: String = "",
    val ADDRESS1: String = "",
    val ADDRESS2: String = "",
    val CITYTOWN: String = "",
    val STATEPRO: String = "",
    val ZIP: String = "",
    val GRIDLAT: Double = 0.0,
    val GRIDLONG: Double = 0.0,
    val ADDR_LOC: String = ""
)

fun String.toAddressSuggestions(json: Json): List<AddressSuggestion> {
    val response = runCatching { json.decodeFromString<TypeAheadAddressResponse>(this) }.getOrNull()
        ?: return emptyList()
    return response.data.addressData.map { it.toAddressSuggestion() }
}

private fun TypeAheadAddressDto.toAddressSuggestion(): AddressSuggestion {
    val addrLoc = ADDR_LOC.clean()
    val streetLine = listOfNotNull(ADDRESS1.clean(), ADDRESS2.clean()).joinToString(" ").ifBlank { null }
    val stateZip = listOfNotNull(STATEPRO.clean(), ZIP.clean()).joinToString(" ").ifBlank { null }
    val cityStateZip = listOfNotNull(CITYTOWN.clean(), stateZip).joinToString(", ")
    val shortAddress = listOfNotNull(streetLine, cityStateZip.ifBlank { null }).joinToString(", ")
    val fullAddress = listOfNotNull(addrLoc, shortAddress.ifBlank { null }).joinToString(", ")

    return AddressSuggestion(
        title = addrLoc ?: streetLine ?: shortAddress,
        subtitle = if (addrLoc != null) shortAddress else cityStateZip,
        fullAddress = fullAddress,
        latitude = GRIDLAT.takeIf { it != 0.0 },
        longitude = GRIDLONG.takeIf { it != 0.0 }
    )
}

/** Unlike the legacy client's reference-equality `!= "null"` check (always true), this actually drops blank/"null" fields. */
private fun String.clean(): String? = trim().takeIf { it.isNotEmpty() && !it.equals("null", ignoreCase = true) }
