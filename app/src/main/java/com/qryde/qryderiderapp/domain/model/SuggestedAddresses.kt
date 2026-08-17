package com.qryde.qryderiderapp.domain.model

data class SuggestedAddresses(
    val recentAddresses: List<RecentAddressSuggestion>,
    val savedTripAddresses: List<SavedTripAddress>
)

data class RecentAddressSuggestion(
    val street: String,
    val city: String,
    val zip: String,
    val stateCode: String,
    val countryCode: String,
    val latitude: String,
    val longitude: String
)

data class SavedTripAddress(
    val operatorId: String,
    val estOperatorId: String,
    val supplierId: String,
    val communityId: String,
    val estDistance: String,
    val estTravelTime: String,
    val fare: String,
    val mobility: String,
    val purpose: String,
    val passengersCount: String,
    val additionalPassengersDetails: String,
    val operatorAddress: String,
    val pickupStreet: String,
    val pickupCity: String,
    val pickupZip: String,
    val pickupStateCode: String,
    val pickupCountryCode: String,
    val pickupLatitude: String,
    val pickupLongitude: String,
    val dropoffStreet: String,
    val dropoffCity: String,
    val dropoffZip: String,
    val dropoffStateCode: String,
    val dropoffCountryCode: String,
    val dropoffLatitude: String,
    val dropoffLongitude: String,
    val tripId: String
)
