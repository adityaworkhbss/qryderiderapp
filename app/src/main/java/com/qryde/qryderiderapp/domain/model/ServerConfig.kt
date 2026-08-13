package com.qryde.qryderiderapp.domain.model

data class ServerConfig(
    val endpoints: Map<String, String>
) {
    fun urlFor(key: String): String? = endpoints[key]
}
