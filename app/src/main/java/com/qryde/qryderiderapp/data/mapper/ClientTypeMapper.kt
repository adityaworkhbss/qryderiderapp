package com.qryde.qryderiderapp.data.mapper

import com.qryde.qryderiderapp.domain.model.OeRegistryValues
import org.json.JSONException
import org.json.JSONObject

const val DEFAULT_CLIENT_TYPE = "NONPT1"
private const val CP_SITE_CONFIG_KEY = "CP_SiteConfig"

fun OeRegistryValues.resolveClientType(preferredCommunityId: String): String {
    val rawConfig = valueFor(CP_SITE_CONFIG_KEY) ?: return DEFAULT_CLIENT_TYPE
    return try {
        val siteConfig = JSONObject(rawConfig).getJSONObject(CP_SITE_CONFIG_KEY)
        val siteObj = siteConfig.optJSONObject(preferredCommunityId.uppercase()) ?: return DEFAULT_CLIENT_TYPE
        siteObj.optString("ClientType", DEFAULT_CLIENT_TYPE).ifBlank { DEFAULT_CLIENT_TYPE }
    } catch (e: JSONException) {
        DEFAULT_CLIENT_TYPE
    }
}
