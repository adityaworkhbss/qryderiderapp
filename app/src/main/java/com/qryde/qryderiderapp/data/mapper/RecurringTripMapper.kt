package com.qryde.qryderiderapp.data.mapper

import com.qryde.qryderiderapp.domain.model.RecurringTrip

private val RECORD_SEPARATOR = 15.toChar()
private val FIELD_SEPARATOR = 14.toChar()
private const val NO_DATA_FOUND = "no_data_found"
private const val NOK = "nok"
private const val MIN_7GM_FIELD_COUNT = 30
private const val NEMT_COLUMN_SEPARATOR = "^"
private const val UNSCHEDULED_TRIP_ID_PREFIX = "t"

/** 7GM response: "7GM~<record>(char15)<record>(char15)..." or "7GM~NO_DATA_FOUND". */
fun String.toRecurringTripsFrom7GM(): List<RecurringTrip> {
    val payload = split('~').getOrNull(1)?.trim().orEmpty()
    if (payload.isEmpty() || payload.equals(NO_DATA_FOUND, ignoreCase = true)) return emptyList()

    return payload.split(RECORD_SEPARATOR)
        .mapNotNull { it.to7GMTripOrNull() }
        .filterNot { it.tripId.startsWith(UNSCHEDULED_TRIP_ID_PREFIX, ignoreCase = true) }
}

/**
 * Each record is (char14)-separated with ~45 fields; only a subset is used
 * (mirrors the legacy client's process7GM() switch). Records shorter than
 * [MIN_7GM_FIELD_COUNT] fields are skipped, matching the legacy guard.
 */
private fun String.to7GMTripOrNull(): RecurringTrip? {
    val fields = split(FIELD_SEPARATOR)
    if (fields.size < MIN_7GM_FIELD_COUNT) return null
    fun at(index: Int) = fields.getOrNull(index)?.trim().orEmpty()

    return RecurringTrip(
        tripId = at(13),
        travelDate = at(0),
        pickupTime = at(1),
        dropoffTime = at(22),
        pickupAddress = at(2),
        dropoffAddress = at(3),
        tripFare = at(5),
        collectedFare = at(12),
        disposition = at(17),
        payUpFront = at(30),
        operOnlinePayment = at(15),
        operId = at(11),
        providerName = at(35).ifBlank { at(8) },
        operatorPhone = at(31),
        mobilities = "",
        recurringDaysLabel = "",
        notes = at(20)
    )
}

/**
 * NEMT tenant API response: "<status>^..." on failure, or one or more
 * (~)-separated records, each (^)-separated - mirrors the legacy client's
 * processCPTrips(). Distinct wire format from 7GM since this hits the
 * community's own tenant endpoint directly, not QTIP_API.
 */
fun String.toRecurringTripsFromTenantApi(): List<RecurringTrip> {
    if (isEmpty() || !contains(NEMT_COLUMN_SEPARATOR)) return emptyList()

    val statusFields = split(NEMT_COLUMN_SEPARATOR)
    if (statusFields.getOrNull(0).equals(NOK, ignoreCase = true)) return emptyList()

    return split('~')
        .mapNotNull { it.toTenantTripOrNull() }
        .filterNot { it.tripId.startsWith(UNSCHEDULED_TRIP_ID_PREFIX, ignoreCase = true) }
}

private fun String.toTenantTripOrNull(): RecurringTrip? {
    val fields = split(NEMT_COLUMN_SEPARATOR)
    if (fields.size < 11) return null
    fun at(index: Int) = fields.getOrNull(index)?.trim().orEmpty()

    val recurringDaysLabel = at(1).split(",")
        .mapNotNull { it.split("|").firstOrNull()?.trim()?.takeIf { day -> day.isNotEmpty() } }
        .joinToString(",")

    return RecurringTrip(
        tripId = at(0),
        travelDate = at(4),
        pickupTime = "",
        dropoffTime = "",
        pickupAddress = at(2),
        dropoffAddress = at(3),
        tripFare = at(9),
        collectedFare = "",
        disposition = at(10),
        payUpFront = "",
        operOnlinePayment = "",
        operId = "",
        providerName = "",
        operatorPhone = "",
        mobilities = at(6),
        recurringDaysLabel = recurringDaysLabel,
        notes = ""
    )
}
