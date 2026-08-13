package com.qryde.qryderiderapp.domain.model

data class OeRegistryValues(
    val values: Map<String, String>
) {
    fun valueFor(key: String): String? = values[key]
}
