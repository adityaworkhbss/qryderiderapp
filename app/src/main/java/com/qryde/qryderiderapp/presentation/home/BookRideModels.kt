package com.qryde.qryderiderapp.presentation.home

enum class BookingStep {
    SEARCH,
    ADDRESS_SETUP,
    RIDE_DETAILS,
    CHOOSE_SERVICE
}

enum class AddressField {
    PICKUP,
    DROPOFF
}

enum class DatePickerTarget {
    SINGLE,
    START,
    END
}

data class RecentAddress(
    val id: String,
    val title: String,
    val subtitle: String,
    val destinationAddress: String = title
)

enum class QuickPlaceIcon { HOME, OFFICE, OTHER }

/** A saved trip's destination, shown as a one-tap shortcut (e.g. "Home", "Work"). */
data class QuickPlace(
    val id: String,
    val label: String,
    val subtitle: String,
    val destinationAddress: String,
    val icon: QuickPlaceIcon
)

data class ServiceOption(
    val id: String,
    val name: String,
    val durationMinutes: Int,
    val distanceMiles: Double,
    val fare: Int,
    val hasInfo: Boolean = false
)

val SampleServiceOptions = listOf(
    ServiceOption("fixed_bus_route", "Fixed Bus Route", durationMinutes = 50, distanceMiles = 9.8, fare = 60, hasInfo = true),
    ServiceOption("paratransit", "Paratransit", durationMinutes = 20, distanceMiles = 9.8, fare = 60),
    ServiceOption("monthly_ride_services", "Monthly Ride Services", durationMinutes = 10, distanceMiles = 9.8, fare = 60),
    ServiceOption("evening_ride_services", "Evening Ride Services", durationMinutes = 10, distanceMiles = 9.8, fare = 60),
    ServiceOption("microtransit", "Microtransit", durationMinutes = 80, distanceMiles = 9.8, fare = 60),
    ServiceOption("uber", "Uber", durationMinutes = 15, distanceMiles = 9.8, fare = 75)
)
