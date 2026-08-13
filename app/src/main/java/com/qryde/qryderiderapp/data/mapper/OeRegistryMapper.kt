package com.qryde.qryderiderapp.data.mapper

import com.qryde.qryderiderapp.domain.model.OeRegistryValues

/**
 * Wire format for 17CV (and similar "registry values" commands): everything after
 * the first '~' is a flat KEY/VALUE list, rows separated by ASCII Shift-In (0x0F)
 * and key/value within a row separated by ASCII Shift-Out (0x0E) - the same
 * char15/char14 delimiters the legacy client used. They're non-printable, which is
 * why a pasted sample response looks like keys and values are run together.
 */
fun String.toOeRegistryValues(): OeRegistryValues {
    val payload = substringAfter(COMMAND_SEPARATOR, missingDelimiterValue = this)
    val values = payload.split(ROW_SEPARATOR)
        .mapNotNull { row ->
            val columns = row.split(COLUMN_SEPARATOR, limit = 2)
            if (columns.size != 2) return@mapNotNull null
            val key = columns[0].trim()
            if (key.isEmpty()) null else key to columns[1]
        }
        .toMap()

    return OeRegistryValues(values)
}

private const val COMMAND_SEPARATOR = '~'
private val ROW_SEPARATOR = 15.toChar()
private val COLUMN_SEPARATOR = 14.toChar()
