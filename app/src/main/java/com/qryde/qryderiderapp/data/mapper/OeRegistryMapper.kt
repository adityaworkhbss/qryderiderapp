package com.qryde.qryderiderapp.data.mapper

import com.qryde.qryderiderapp.domain.model.OeRegistryValues


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
