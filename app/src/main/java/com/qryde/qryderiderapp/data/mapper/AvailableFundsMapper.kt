package com.qryde.qryderiderapp.data.mapper

fun String.toAvailableFundsAmount(): Double {
    val payload = substringAfter('~', missingDelimiterValue = "").trim()
    return payload.toDoubleOrNull() ?: 0.0
}
