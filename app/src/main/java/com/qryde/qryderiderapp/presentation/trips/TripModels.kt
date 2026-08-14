package com.qryde.qryderiderapp.presentation.trips

enum class TripStatus {
    UPCOMING,
    COMPLETED,
    CANCELLED
}

data class TripSummary(
    val id: String,
    val status: TripStatus,
    val dateLabel: String,
    val pickup: String,
    val dropoff: String,
    val dropoffDetail: String? = null
)

val SampleTrips = listOf(
    TripSummary(
        id = "TR-2568",
        status = TripStatus.UPCOMING,
        dateLabel = "Tomorrow, 9:00 AM",
        pickup = "456 Elm St, Brooklyn",
        dropoff = "LaGuardia Airport",
        dropoffDetail = "Terminal C, Gate 12"
    ),
    TripSummary(
        id = "TR-2569",
        status = TripStatus.COMPLETED,
        dateLabel = "Tomorrow, 9:00 AM",
        pickup = "456 Elm St, Brooklyn",
        dropoff = "LaGuardia Airport",
        dropoffDetail = "Terminal C, Gate 12"
    ),
    TripSummary(
        id = "TR-2570",
        status = TripStatus.COMPLETED,
        dateLabel = "Fri, 6:15 PM",
        pickup = "789 Broadway, Manhattan",
        dropoff = "Penn Station",
        dropoffDetail = "Platform 5"
    ),
    TripSummary(
        id = "TR-2571",
        status = TripStatus.CANCELLED,
        dateLabel = "Mon, 8:00 AM",
        pickup = "123 Park Ave, New York",
        dropoff = "JFK International Airport",
        dropoffDetail = "Terminal 4, Gate B23"
    )
)
