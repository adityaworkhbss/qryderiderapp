package com.qryde.qryderiderapp.data.mapper

import com.qryde.qryderiderapp.domain.model.RecentAddressSuggestion
import com.qryde.qryderiderapp.domain.model.SavedTripAddress
import com.qryde.qryderiderapp.domain.model.SuggestedAddresses

private const val NULL_PLACEHOLDER = "..."
private const val NO_DATA_FOUND = "no_data_found"
private val GROUP_SEPARATOR = 12.toChar()
private val RECORD_SEPARATOR = 15.toChar()
private val FIELD_SEPARATOR = 14.toChar()

private fun String.isMissing(): Boolean =
    isEmpty() || equals(NULL_PLACEHOLDER, ignoreCase = true) || equals(NO_DATA_FOUND, ignoreCase = true)

/**
 * 5FAT response: "5FAT~<recentAddresses>(char12)<savedTripAddresses>", where
 * each group is a (char15)-separated list of (char14)-separated records.
 */
fun String.toSuggestedAddresses(): SuggestedAddresses {
    val payload = substringAfter('~', missingDelimiterValue = "")
    if (payload.isMissing()) {
        return SuggestedAddresses(emptyList(), emptyList())
    }

    val groups = payload.split(GROUP_SEPARATOR)
    val recentGroup = groups.getOrNull(0).orEmpty()
    val savedGroup = groups.getOrNull(1).orEmpty()

    val recentAddresses = if (!recentGroup.isMissing()) {
        recentGroup.split(RECORD_SEPARATOR)
            .filterNot { it.isMissing() }
            .map { it.toRecentAddressSuggestion() }
    } else {
        emptyList()
    }

    val savedTripAddresses = if (!savedGroup.isMissing()) {
        savedGroup.split(RECORD_SEPARATOR)
            .filterNot { it.isMissing() }
            .map { it.toSavedTripAddress() }
    } else {
        emptyList()
    }

    return SuggestedAddresses(recentAddresses, savedTripAddresses)
}

private fun String.toRecentAddressSuggestion(): RecentAddressSuggestion {
    val fields = split(FIELD_SEPARATOR)
    fun at(index: Int) = fields.getOrNull(index).orEmpty()
    return RecentAddressSuggestion(
        street = at(0),
        city = at(1),
        zip = at(2),
        stateCode = at(3),
        countryCode = at(4),
        latitude = at(5),
        longitude = at(6)
    )
}

private fun String.toSavedTripAddress(): SavedTripAddress {
    val fields = split(FIELD_SEPARATOR)
    fun at(index: Int) = fields.getOrNull(index).orEmpty()
    return SavedTripAddress(
        operatorId = at(0),
        estOperatorId = at(1),
        supplierId = at(2),
        communityId = at(3),
        estDistance = at(4),
        estTravelTime = at(5),
        fare = at(6),
        mobility = at(7),
        purpose = at(8),
        passengersCount = at(9),
        additionalPassengersDetails = at(10),
        operatorAddress = at(11),
        pickupStreet = at(12),
        pickupCity = at(13),
        pickupZip = at(14),
        pickupStateCode = at(15),
        pickupCountryCode = at(16),
        pickupLatitude = at(17),
        pickupLongitude = at(18),
        dropoffStreet = at(19),
        dropoffCity = at(20),
        dropoffZip = at(21),
        dropoffStateCode = at(22),
        dropoffCountryCode = at(23),
        dropoffLatitude = at(24),
        dropoffLongitude = at(25),
        tripId = at(26)
    )
}
