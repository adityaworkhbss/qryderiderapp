package com.qryde.qryderiderapp.data.mapper

import com.qryde.qryderiderapp.domain.model.CommunitySiteConfig
import com.qryde.qryderiderapp.domain.model.OeRegistryValues
import org.json.JSONArray
import org.json.JSONObject

private const val CP_SITE_CONFIG_KEY = "CP_SiteConfig"

fun OeRegistryValues.communitySiteConfig(communityId: String): CommunitySiteConfig {
    val siteObj = communitySiteConfigJson(communityId)
        ?: return CommunitySiteConfig(rawJson = null, values = emptyMap())

    val values = mutableMapOf<String, String>()
    val keys = siteObj.keys()
    while (keys.hasNext()) {
        val key = keys.next()
        val value = siteObj.opt(key)
        if (value !is JSONObject && value !is JSONArray) {
            values[key] = value.toString()
        }
    }
    return CommunitySiteConfig(rawJson = siteObj.toString(), values = values)
}

private fun OeRegistryValues.communitySiteConfigJson(communityId: String): JSONObject? {
    val rawConfig = valueFor(CP_SITE_CONFIG_KEY) ?: return null
    return try {
        JSONObject(rawConfig).getJSONObject(CP_SITE_CONFIG_KEY).optJSONObject(communityId.uppercase())
    } catch (e: Exception) {
        null
    }
}
