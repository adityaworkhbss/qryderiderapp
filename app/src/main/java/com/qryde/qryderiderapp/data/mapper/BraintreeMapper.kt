package com.qryde.qryderiderapp.data.mapper

private const val NULL_PLACEHOLDER = "..."
private val COLUMN_SEPARATOR = 14.toChar()

fun String.toBraintreeCustomerId(): String? {
    val payload = substringAfter('~', missingDelimiterValue = this)
    val customerId = payload.split(COLUMN_SEPARATOR).firstOrNull()?.trim().orEmpty()
    return customerId.takeIf { it.isNotEmpty() && !it.equals(NULL_PLACEHOLDER, ignoreCase = true) }
}

fun String.toBraintreeClientToken(): String =
    substringAfter('~', missingDelimiterValue = this).trim()
