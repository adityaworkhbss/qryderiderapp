package com.qryde.qryderiderapp.data.mapper

import com.qryde.qryderiderapp.domain.model.Transaction

private val RECORD_SEPARATOR = 15.toChar()
private val FIELD_SEPARATOR = 14.toChar()
private const val NO_DATA_FOUND = "no_data_found"
private const val NOK = "nok"

fun String.toTransactions(): List<Transaction> {
    val payload = substringAfter('~', missingDelimiterValue = "").trim()
    if (payload.isEmpty() || payload.equals(NOK, ignoreCase = true) || payload.equals(NO_DATA_FOUND, ignoreCase = true)) {
        return emptyList()
    }

    val records = payload.split(RECORD_SEPARATOR)
    if (records.size <= 1) return emptyList()

    return records.drop(1)
        .filter { !it.equals(NO_DATA_FOUND, ignoreCase = true) }
        .map { it.toTransaction() }
}

fun String.toEchoedUserFsId(): String? {
    val payload = substringAfter('~', missingDelimiterValue = "").trim()
    if (payload.isEmpty()) return null
    return payload.split(RECORD_SEPARATOR).firstOrNull()?.trim()?.takeIf { it.isNotEmpty() }
}

private fun String.toTransaction(): Transaction {
    val fields = split(FIELD_SEPARATOR)
    fun at(index: Int) = fields.getOrNull(index)?.trim().orEmpty()
    return Transaction(
        transactionId = at(1),
        userId = at(2),
        transactionFor = at(3),
        dateTime = at(0),
        depositAmount = at(6),
        paidAmount = at(7)
    )
}
