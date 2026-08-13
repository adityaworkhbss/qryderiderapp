package com.qryde.qryderiderapp.data.mapper

import com.qryde.qryderiderapp.domain.model.ServerConfig

/**
 * Wire format: semicolon-separated "KEY~VALUE" fields, e.g.
 *   QTIP2_TestServer_BASEURL~https://stgq.qryde.net;QREST2_TestServer_IPPORT~reststg.qryde.net,443;...
 * VALUE is either already a full URL (has a scheme) or a bare "host,port" pair
 * that needs a scheme and the comma swapped for a colon. Keys are not at fixed
 * positions and the set of keys can vary (different regions, optional services),
 * so every entry is parsed by its own key rather than by index.
 */
fun String.toServerConfig(): ServerConfig {
    val endpoints = split(";")
        .mapNotNull { entry ->
            val key = entry.substringBefore("~", missingDelimiterValue = "").trim()
            val rawValue = entry.substringAfter("~", missingDelimiterValue = "").trim()
            if (key.isEmpty() || rawValue.isEmpty()) null else key to rawValue.toResolvedUrl()
        }
        .toMap()

    require(endpoints.isNotEmpty()) { "Config server response did not contain any endpoints: $this" }

    return ServerConfig(endpoints)
}

private fun String.toResolvedUrl(): String =
    if (startsWith("http://", ignoreCase = true) || startsWith("https://", ignoreCase = true)) {
        this
    } else {
        // "host,port" -> "https://host:port"
        "https://${replace(",", ":")}"
    }
