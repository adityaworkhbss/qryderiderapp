package com.qryde.qryderiderapp.domain.model

data class CommunitySiteConfig(
    val rawJson: String?,
    val values: Map<String, String>
) {
    fun valueFor(key: String): String? = values[key]

    fun valueFor(key: String, default: String): String = values[key] ?: default

    fun boolFor(key: String, trueValue: String = "Y"): Boolean =
        values[key]?.equals(trueValue, ignoreCase = true) == true
}
