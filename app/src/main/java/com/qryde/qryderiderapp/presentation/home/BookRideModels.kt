package com.qryde.qryderiderapp.presentation.home

enum class BookingStep {
    SEARCH,
    RIDE_DETAILS,
    CHOOSE_SERVICE
}

enum class DatePickerTarget {
    SINGLE,
    START,
    END
}

data class RecentAddress(
    val id: String,
    val title: String,
    val subtitle: String
)

data class ServiceOption(
    val id: String,
    val name: String,
    val durationMinutes: Int,
    val distanceMiles: Double,
    val fare: Int,
    val hasInfo: Boolean = false
)

val SampleRecentAddresses = listOf(
    RecentAddress("1", "123, Maple Street", "Sector 15, Noida, Uttar Pradesh, 201301, India"),
    RecentAddress("2", "456, Rose Avenue, Block B,", "Sector 18, Haryana, Gurgaon 122018"),
    RecentAddress("3", "789, Tulip Street, Unit 12,", "Block C, Sector 20, Gurugram, Haryana")
)

val SampleServiceOptions = listOf(
    ServiceOption("fixed_bus_route", "Fixed Bus Route", durationMinutes = 50, distanceMiles = 9.8, fare = 60, hasInfo = true),
    ServiceOption("paratransit", "Paratransit", durationMinutes = 20, distanceMiles = 9.8, fare = 60),
    ServiceOption("monthly_ride_services", "Monthly Ride Services", durationMinutes = 10, distanceMiles = 9.8, fare = 60),
    ServiceOption("evening_ride_services", "Evening Ride Services", durationMinutes = 10, distanceMiles = 9.8, fare = 60),
    ServiceOption("microtransit", "Microtransit", durationMinutes = 80, distanceMiles = 9.8, fare = 60),
    ServiceOption("uber", "Uber", durationMinutes = 15, distanceMiles = 9.8, fare = 75)
)
