package com.qryde.qryderiderapp.domain.model

/**
 * Resolved endpoints from the config-discovery socket, keyed by the server's own
 * key names (e.g. "QREST2_TestServer_IPPORT", "QTIP3_TestServer_BASEURL"). The set
 * of keys varies by environment/region - e.g. a "2" vs "3" region suffix, or
 * services like MNJS that aren't always present - so this holds whatever came
 * back rather than assuming a fixed shape. See ServerConfigMapper for parsing.
 */
data class ServerConfig(
    val endpoints: Map<String, String>
) {
    fun urlFor(key: String): String? = endpoints[key]
}
